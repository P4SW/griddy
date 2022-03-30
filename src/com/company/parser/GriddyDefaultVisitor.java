/* Generated By:JavaCC: Do not edit this line. GriddyDefaultVisitor.java Version 7.0.10 */
package com.company.parser;

import com.company.*;

public class GriddyDefaultVisitor implements GriddyVisitor{
  public Object defaultVisit(SimpleNode node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(SimpleNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTStart node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTSetup node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTGame node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTBoard node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTAssign node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTIdent node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTExpr node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTAdd node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTMult node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTInteger node, Object data){
    return defaultVisit(node, data);
  }
}
/* JavaCC - OriginalChecksum=c0743ea4de764939566b92d034e7ecdb (do not edit this line) */
