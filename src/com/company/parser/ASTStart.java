/* Generated By:JJTree: Do not edit this line. ASTStart.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.company.parser;

import com.company.*;

public
class ASTStart extends SimpleNode {
  public ASTStart(int id) {
    super(id);
  }

  public ASTStart(Griddy p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(GriddyVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=df3fd2090106ca42e55da8d8433f124e (do not edit this line) */