/* Generated By:JJTree: Do not edit this line. ASTMult.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.company.parser;

import com.company.*;

public
class ASTMult extends SimpleNode {
  public ASTMult(int id) {
    super(id);
  }

  public ASTMult(Griddy p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(GriddyVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=749f08591f9aa79eef40320d884e4939 (do not edit this line) */
