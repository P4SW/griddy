package com.company.parser;

import java.util.ArrayList;

public class Visitor extends GriddyDefaultVisitor {
    /** Throw error in case a base AST node is encountered. */
    public Object visit(SimpleNode node, Object data){
        throw new RuntimeException("Encountered SimpleNode");
    }

    /** Root */
    public Object visit(ASTStart node, Object data){
        StringBuilder output = (StringBuilder) data;

        // Include C libraries that might be needed:
        output.append("/* === Code generated by Griddy compiler === */\n")
                .append("#include <stdio.h>\n")
                .append("#include <stdlib.h>\n")
                .append("#include <string.h>\n")
                .append("\nint main(int argc, char *argv[]){\n"); // main begin.

        // Setup phase:
        node.jjtGetChild(0).jjtAccept(this, data);
        output.append("\n");

        // Game phase:
        node.jjtGetChild(1).jjtAccept(this, data);

        return output.append("\nreturn 0;\n}\n"); // main end.
    }

    /**
     * `echo` print statement, which maps to C's `printf`.
     * */
    public Object visit(ASTEcho node, Object data) {
        StringBuilder output = (StringBuilder) data;

        String argType = GriddyTreeConstants.jjtNodeName[node.jjtGetChild(0).getId()];
        Object argValue = node.jjtGetChild(0).jjtGetValue();

        if (argType.equals("Ident")) {
            ArrayList<Node> prevAssign = getAssignedInScope(node, node.jjtGetChild(0).getName());
            Node assocNode = prevAssign
                    .get(prevAssign.toArray().length - 1)
                    .jjtGetChild(1);
            argType = GriddyTreeConstants.jjtNodeName[assocNode.getId()];
            argValue = node
                    .jjtGetChild(0)
                    .getName();
        }

        return switch (argType) {
            case "Integer" -> output
                    .append("printf(\"%d\", ")
                    .append(argValue)
                    .append(");\n");
            case "String" -> {
                output.append("printf(\"%s\", ");
                node.jjtGetChild(0).jjtAccept(this, data);
                yield output.append(");\n");
            }
            default -> throw new RuntimeException("Can't echo value of unknown type");
        };
    }

    public Object visit(ASTSetup node, Object data){
        ((StringBuilder) data).append("/*  SETUP   */\n");
        for (Node child : node.children)
            child.jjtAccept(this, data);

        return data;
    }

    public Object visit(ASTGame node, Object data){
        ((StringBuilder) data).append("/*  GAME    */\n");
        for (Node child : node.children)
            child.jjtAccept(this, data);

        return data;
    }

    public Object visit(ASTBoard node, Object data){
        return data;
    }

    /**
     * Check if identifier name has been declared in scope.
     * @param node start
     * @param name identifier
     * @return status
     */
    boolean isDeclaredInScope(Node node, String name) {
        if (node.jjtGetParent() == null) return false;

        for (Node c : node.jjtGetParent().getChildren()) {
            if (c == node) break;

            if (GriddyTreeConstants.jjtNodeName[c.getId()].equals("Assign")) {
                if (c.jjtGetChild(0).getName().equals(name))
                    return true;
            }
        }

        return isDeclaredInScope(node.jjtGetParent(), name);
    }

    /**
     * Get all previous assignments of identifier name.
     * @param node start
     * @param name identifier
     * @return previous assignment nodes
     */
    ArrayList<Node> getAssignedInScope(Node node, String name) {
        ArrayList<Node> output = new ArrayList<>();

        if (node.jjtGetParent() != null)
            for (Node c : node.jjtGetParent().getChildren()) {
                if (c == node) break;

                if (GriddyTreeConstants.jjtNodeName[c.getId()].equals("Assign")
                        && c.jjtGetChild(0).getName().equals(name)
                ) output.add(c);
                else output.addAll(getAssignedInScope(node.jjtGetParent(), name));
            }

        return output;
    }

    /**
     * Variable assignment nodes, e.g. `my_var = 42`.
     */
    public Object visit(ASTAssign node, Object data) {
        StringBuilder output = (StringBuilder) data;

        Node identNode = node.jjtGetChild(0);
        Node valueNode = node.jjtGetChild(1);
        String ident = identNode.getName();
        Object value = valueNode.jjtGetValue();
        String valueType = GriddyTreeConstants.jjtNodeName[valueNode.getId()];

        // Generate code based on whether the identifier being assigned, has already been declared or not:
        if (isDeclaredInScope(identNode, ident))
            return switch (valueType) {
                case "String" -> {
                    // 1. str_ptr = realloc(str_ptr, str_size);
                    // 2. strcpy(str_ptr, str_val);
                    identNode.jjtAccept(this, data);
                    output.append(" = realloc(");
                    identNode.jjtAccept(this, data);
                    output.append(", ")
                            .append(value.toString().length() + 1)
                            .append(");\n")
                            .append("strcpy(");
                    identNode.jjtAccept(this, data);
                    output.append(", ");
                    valueNode.jjtAccept(this, data);
                    yield output.append(");\n");
                }

                // Integer values 0 and >0 used in C boolean expressions instead of bool literals.
                case "Integer", "Bool" -> {
                    // 1. var_name = int_val;
                    identNode.jjtAccept(this, data);
                    output.append(" = ");
                    valueNode.jjtAccept(this, data);
                    yield output.append(";\n");
                }
                // NOTE: Board might not be re-assignable
                case "Board" -> output.append("/* Board declarations not yet implemented... */\n");
                default -> throw new RuntimeException("Encountered invalid value type in assignment.");
            };

        return switch (valueType) {
            // 1. char *str_ptr;
            // 2. str_ptr = calloc(str_size, sizeof(char));
            // 3. strcpy(str_ptr, str_val);
            case "String" -> {
                output.append("char *");
                identNode.jjtAccept(this, data);
                output.append(";\n");
                identNode.jjtAccept(this, data);
                output.append(" = calloc(")
                        .append(value.toString().length() + 1)
                        .append(", sizeof(char));\n")
                        .append("strcpy(");
                identNode.jjtAccept(this, data);
                output.append(", ");
                valueNode.jjtAccept(this, data);
                yield output.append(");\n");
            }

            // Integer values 0 and >0 used in C boolean expressions instead of bool literals.
            // 1. int var_name = int_value;
            case "Integer", "Bool" -> {
                output.append("int ");
                identNode.jjtAccept(this, data);
                output.append(" = ");
                valueNode.jjtAccept(this, data);
                yield output.append(";\n");
            }

            // TODO: Implement assignable board type.
            case "Board" -> output.append("/* Board declarations not yet implemented... */\n");
            default -> throw new RuntimeException("Encountered invalid value type in assignment.");
        };
    }

    public Object visit(ASTAdd node, Object data){
        node.jjtGetChild(0).jjtAccept(this, data);
        ((StringBuilder) data).append("+");
        return node.jjtGetChild(1).jjtAccept(this, data);
    }

    public Object visit(ASTSub node, Object data){
        node.jjtGetChild(0).jjtAccept(this, data);
        ((StringBuilder) data).append("-");
        return node.jjtGetChild(1).jjtAccept(this, data);
    }

    public Object visit(ASTString node, Object data) {
        return ((StringBuilder) data)
                .append("\"")
                .append(node.jjtGetValue())
                .append("\"");
    }
    
    public Object visit(ASTIdent node, Object data) {
        return ((StringBuilder) data).append(node.getName());
    }

    public Object visit(ASTInteger node, Object data) {
        return ((StringBuilder) data).append(node.jjtGetValue());
    }

    public Object visit(ASTBool node, Object data) {
        return ((StringBuilder) data).append("true".equals(node.jjtGetValue()) ? "1" : "0");
    }

    public Object visit(ASTDiv node, Object data) {
        node.jjtGetChild(0).jjtAccept(this, data);
        ((StringBuilder) data).append("/");
        return node.jjtGetChild(1).jjtAccept(this, data);
    }

    public Object visit(ASTMod node, Object data) {
        node.jjtGetChild(0).jjtAccept(this, data);
        ((StringBuilder) data).append("%");
        return node.jjtGetChild(1).jjtAccept(this, data);
    }

    public Object visit(ASTMul node, Object data) {
        node.jjtGetChild(0).jjtAccept(this, data);
        ((StringBuilder) data).append("*");
        return node.jjtGetChild(1).jjtAccept(this, data);
    }
}
