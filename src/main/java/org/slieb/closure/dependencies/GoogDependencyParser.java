package org.slieb.closure.dependencies;


import com.google.common.collect.ImmutableSet;
import com.google.javascript.jscomp.NodeUtil;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.parsing.Config;
import com.google.javascript.jscomp.parsing.ParserRunner;
import com.google.javascript.rhino.ErrorReporter;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.SimpleErrorReporter;
import com.google.javascript.rhino.Token;
import org.slieb.dependencies.DependencyParser;
import slieb.kute.api.Resource;

import static com.google.javascript.jscomp.NodeUtil.visitPreOrder;
import static com.google.javascript.jscomp.parsing.ParserRunner.createConfig;

public class GoogDependencyParser implements DependencyParser<Resource.Readable, GoogDependencyNode> {

    private static final Config CONFIG = createConfig(true, Config.LanguageMode.ECMASCRIPT6_STRICT, true, null);

    private static final ErrorReporter REPORTER = new SimpleErrorReporter();

    public GoogDependencyParser() {
    }

    @Override
    public GoogDependencyNode parse(Resource.Readable resource) {
        try {
            SourceFile sourceFile = GoogResources.getSourceFileFromResource(resource);
            ParserRunner.ParseResult result = ParserRunner.parse(sourceFile, sourceFile.getCode(), CONFIG, REPORTER);
            return new Visitor(resource).parse(result.ast);
        } catch (Exception ioException) {
            throw new RuntimeException(String.format("Could not parse dependencies of %s", resource), ioException);
        }
    }

}

class Builder {

    private Boolean isBaseFile = false;
    private final ImmutableSet.Builder<String> provides, requires;
    private Resource.Readable resource;

    public Builder(Resource.Readable resource) {
        this.resource = resource;
        this.provides = new ImmutableSet.Builder<>();
        this.requires = new ImmutableSet.Builder<>();
    }

    public Builder addProvide(String provide) {
        provides.add(provide);
        return this;
    }

    public Builder addRequire(String require) {
        requires.add(require);
        return this;
    }

    public Builder isBase() {
        isBaseFile = true;
        return this;
    }

    public GoogDependencyNode build() {
        return new GoogDependencyNode(resource, provides.build(), requires.build(), isBaseFile);
    }
}

class Visitor implements NodeUtil.Visitor {

    private final Builder builder;

    private boolean isDone = false;

    public Visitor(Resource.Readable resource) {
        this.builder = new Builder(resource);
    }

    @Override
    public void visit(Node node) {
        if (isDone || !node.hasChildren()) return;

        switch (node.getType()) {
            case Token.CALL:
                Node callChild = node.getFirstChild();
                if (callChild.isGetProp()) {
                    if (callChild.getQualifiedName() != null) {
                        switch (callChild.getQualifiedName()) {
                            case "goog.provide":
                                builder.addProvide(node.getChildAtIndex(1).getString());
                                break;
                            case "goog.require":
                                builder.addRequire(node.getChildAtIndex(1).getString());
                                break;
                        }
                    }
                }
                break;
            case Token.ASSIGN:
                Node assignChild = node.getFirstChild();
                if (assignChild.isGetProp()) {
                    String name = assignChild.getQualifiedName();
                    if (name != null && name.equals("goog.base")) {
                        isDone = true;
                        builder.isBase();
                    }
                }
                break;
        }
    }


    private boolean shouldVisitChildren(Node node) {
        if (isDone) return false;

        switch (node.getType()) {
            case Token.SCRIPT:
            case Token.EXPR_RESULT:
            case Token.IF:
            case Token.BLOCK:
                return true;
            case Token.FUNCTION:
            case Token.CALL:
            case Token.GETPROP:
            case Token.ASSIGN:
            default:
                return false;
        }
    }

    public GoogDependencyNode parse(Node rootNode) {
        visitPreOrder(rootNode, this, this::shouldVisitChildren);
        return builder.build();
    }

}


