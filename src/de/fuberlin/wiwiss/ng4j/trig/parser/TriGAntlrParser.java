// $ANTLR 2.7.2: "trig.g" -> "TriGAntlrParser.java"$

package de.fuberlin.wiwiss.ng4j.trig.parser ;
import de.fuberlin.wiwiss.ng4j.trig.AntlrUtils ;
import de.fuberlin.wiwiss.ng4j.trig.TriGParserEventHandler ;
import java.io.* ;
import antlr.TokenStreamRecognitionException ;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class TriGAntlrParser extends antlr.LLkParser       implements TriGAntlrParserTokenTypes
 {

	// Extra code for the parser.

	boolean emitListTypeQuad = false ;

	TriGAntlrLexer lexer = null ;
	public void setLexer(TriGAntlrLexer _lexer) { lexer = _lexer ; }

	// Internallly generated anon id.  Avoid clash with _:xxx
	private int anonId = 0 ;
	private String genAnonId() { return "=:"+(anonId++) ; }

	private TriGParserEventHandler handler = null ;

	public void setEventHandler(TriGParserEventHandler h) { this.handler = h ; }

	private void startDocument()
	{
		if ( handler == null )
			throw new RuntimeException("TriGAntlrParser: No sink specified") ;
		handler.startDocument() ;
	}

	private void endDocument() { handler.endDocument() ; }


	private void startGraph(AST label)
	{
		handler.startGraph(lexer.getLine(), label) ;
	}

	private void endGraph(AST label)
	{
		handler.endGraph(lexer.getLine(), label) ;
	}

	private AST currentGraphName = null;

    private void emitQuad(AST subj, AST prop, AST obj)
	{ 
		handler.quad(lexer.getLine(), subj, prop, obj, currentGraphName);
	}

	private void directive(AST directive, AST arg)
	{
		handler.directive(lexer.getLine(), directive, new AST[]{arg});
	}

	private void directive(AST directive, AST arg1, AST arg2)
	{
		handler.directive(lexer.getLine(), directive, new AST[]{arg1, arg2});
	}

	public void reportError(RecognitionException ex)
	{
		handler.error(ex, "TriG error: ["+ex.line+":"+ex.column+"] "+ex.getMessage());
    }

    /** Parser error-reporting function can be overridden in subclass */
    public void reportError(String s)
    {
	    //System.err.println("TriGAntlrParser(s): "+s);
		handler.error(null, "TriGAntlrParser(s): ["+lexer.getLine()+":"+lexer.getColumn()+"] "+s) ;
    }

protected TriGAntlrParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public TriGAntlrParser(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected TriGAntlrParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public TriGAntlrParser(TokenStream lexer) {
  this(lexer,1);
}

public TriGAntlrParser(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void document() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST document_AST = null;
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				startDocument() ;
			}
			{
			_loop3:
			do {
				switch ( LA(1)) {
				case AT_PREFIX:
				{
					n3Directive();
					break;
				}
				case QNAME:
				case URIREF:
				{
					namedGraph();
					break;
				}
				default:
				{
					break _loop3;
				}
				}
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				endDocument() ;
			}
			AST tmp1_AST = null;
			tmp1_AST = astFactory.create(LT(1));
			match(Token.EOF_TYPE);
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex) ; throw ex ;
			} else {
				throw ex;
			}
		}
		catch (TokenStreamRecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex.recog) ; throw ex.recog ;
			} else {
				throw ex;
			}
		}
		returnAST = document_AST;
	}
	
	public final void n3Directive() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST n3Directive_AST = null;
		
		n3Directive0();
		match(SEP);
		returnAST = n3Directive_AST;
	}
	
	public final void namedGraph() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namedGraph_AST = null;
		AST label_AST = null;
		
		graphLabel();
		label_AST = (AST)returnAST;
		{
		switch ( LA(1)) {
		case NAME_OP:
		{
			match(NAME_OP);
			break;
		}
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		graph(label_AST);
		returnAST = namedGraph_AST;
	}
	
	public final void n3Directive0() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST n3Directive0_AST = null;
		Token  d = null;
		AST d_AST = null;
		AST ns_AST = null;
		AST u_AST = null;
		
		d = LT(1);
		d_AST = astFactory.create(d);
		match(AT_PREFIX);
		nsprefix();
		ns_AST = (AST)returnAST;
		uriref();
		u_AST = (AST)returnAST;
		if ( inputState.guessing==0 ) {
			directive(d_AST, ns_AST, u_AST);
		}
		returnAST = n3Directive0_AST;
	}
	
	public final void nsprefix() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST nsprefix_AST = null;
		Token  ns = null;
		AST ns_AST = null;
		
		try {      // for error handling
			ns = LT(1);
			ns_AST = astFactory.create(ns);
			astFactory.addASTChild(currentAST, ns_AST);
			match(QNAME);
			if (!( ns.getText().endsWith(":") ))
			  throw new SemanticException(" ns.getText().endsWith(\":\") ");
			nsprefix_AST = (AST)currentAST.root;
		}
		catch (SemanticException ex) {
			if (inputState.guessing==0) {
				
						RecognitionException rEx = 
				new RecognitionException("Illegal prefix: '"+ns.getText()+"'") ; 
						rEx.line = lexer.getLine() ; rEx.column = lexer.getColumn() ; 
						throw rEx ;
					
			} else {
				throw ex;
			}
		}
		returnAST = nsprefix_AST;
	}
	
	public final void uriref() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST uriref_AST = null;
		
		AST tmp4_AST = null;
		tmp4_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp4_AST);
		match(URIREF);
		uriref_AST = (AST)currentAST.root;
		returnAST = uriref_AST;
	}
	
	public final void graphLabel() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST graphLabel_AST = null;
		AST q_AST = null;
		AST u_AST = null;
		
		switch ( LA(1)) {
		case QNAME:
		{
			qname();
			q_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				graphLabel_AST = (AST)currentAST.root;
				graphLabel_AST=q_AST;
				currentAST.root = graphLabel_AST;
				currentAST.child = graphLabel_AST!=null &&graphLabel_AST.getFirstChild()!=null ?
					graphLabel_AST.getFirstChild() : graphLabel_AST;
				currentAST.advanceChildToEnd();
			}
			break;
		}
		case URIREF:
		{
			uriref();
			u_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				graphLabel_AST = (AST)currentAST.root;
				graphLabel_AST=u_AST;
				currentAST.root = graphLabel_AST;
				currentAST.child = graphLabel_AST!=null &&graphLabel_AST.getFirstChild()!=null ?
					graphLabel_AST.getFirstChild() : graphLabel_AST;
				currentAST.advanceChildToEnd();
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = graphLabel_AST;
	}
	
	public final void graph(
		AST label
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST graph_AST = null;
		
		match(LCURLY);
		if ( inputState.guessing==0 ) {
			graph_AST = (AST)currentAST.root;
			
						currentGraphName = label;
						startGraph(label);
						graph_AST = label;
					
			currentAST.root = graph_AST;
			currentAST.child = graph_AST!=null &&graph_AST.getFirstChild()!=null ?
				graph_AST.getFirstChild() : graph_AST;
			currentAST.advanceChildToEnd();
		}
		statements();
		astFactory.addASTChild(currentAST, returnAST);
		if ( inputState.guessing==0 ) {
			
						endGraph(label);
						currentGraphName = null;
					
		}
		match(RCURLY);
		graph_AST = (AST)currentAST.root;
		returnAST = graph_AST;
	}
	
	public final void qname() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST qname_AST = null;
		
		AST tmp7_AST = null;
		tmp7_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp7_AST);
		match(QNAME);
		qname_AST = (AST)currentAST.root;
		returnAST = qname_AST;
	}
	
	public final void statements() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statements_AST = null;
		
		switch ( LA(1)) {
		case QNAME:
		case KW_THIS:
		case STRING:
		case LBRACK:
		case LPAREN:
		case NUMBER:
		case URIREF:
		case UVAR:
		{
			statement();
			{
			switch ( LA(1)) {
			case SEP:
			{
				AST tmp8_AST = null;
				tmp8_AST = astFactory.create(LT(1));
				match(SEP);
				statements();
				break;
			}
			case RCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case RCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = statements_AST;
	}
	
	public final void statement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_AST = null;
		AST subj_AST = null;
		
		subject();
		subj_AST = (AST)returnAST;
		propertyList(subj_AST);
		returnAST = statement_AST;
	}
	
	public final void subject() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST subject_AST = null;
		
		item();
		astFactory.addASTChild(currentAST, returnAST);
		subject_AST = (AST)currentAST.root;
		returnAST = subject_AST;
	}
	
	public final void propertyList(
		AST subj
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST propertyList_AST = null;
		
		switch ( LA(1)) {
		case QNAME:
		case KW_THIS:
		case KW_HAS:
		case KW_A:
		case KW_IS:
		case STRING:
		case EQUAL:
		case ARROW_R:
		case ARROW_L:
		case ARROW_PATH_L:
		case LBRACK:
		case LPAREN:
		case NUMBER:
		case URIREF:
		case UVAR:
		{
			propValue(subj);
			{
			switch ( LA(1)) {
			case SEMI:
			{
				AST tmp9_AST = null;
				tmp9_AST = astFactory.create(LT(1));
				match(SEMI);
				propertyList(subj);
				break;
			}
			case SEP:
			case RCURLY:
			case RBRACK:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case SEP:
		case RCURLY:
		case RBRACK:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = propertyList_AST;
	}
	
	public final void item() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST item_AST = null;
		AST n_AST = null;
		AST n1_AST = null;
		AST n2_AST = null;
		
		node();
		n_AST = (AST)returnAST;
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop23:
		do {
			switch ( LA(1)) {
			case PATH:
			{
				match(PATH);
				node();
				n1_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					
								AST a1 = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(ANON,genAnonId()))) ;
								emitQuad(n_AST, n1_AST, a1) ;
								n_AST = a1 ;
							
				}
				break;
			}
			case RPATH:
			{
				match(RPATH);
				node();
				n2_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					
								AST a2 = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(ANON,genAnonId()))) ;
								emitQuad(a2, n2_AST, n_AST) ;
								n_AST = a2 ;
							
				}
				break;
			}
			default:
			{
				break _loop23;
			}
			}
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			item_AST = (AST)currentAST.root;
			item_AST = n_AST ;
			currentAST.root = item_AST;
			currentAST.child = item_AST!=null &&item_AST.getFirstChild()!=null ?
				item_AST.getFirstChild() : item_AST;
			currentAST.advanceChildToEnd();
		}
		item_AST = (AST)currentAST.root;
		returnAST = item_AST;
	}
	
	public final void propValue(
		AST subj
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST propValue_AST = null;
		AST v1_AST = null;
		AST v2_AST = null;
		
		switch ( LA(1)) {
		case QNAME:
		case KW_THIS:
		case KW_HAS:
		case KW_A:
		case STRING:
		case EQUAL:
		case ARROW_R:
		case ARROW_L:
		case ARROW_PATH_L:
		case LBRACK:
		case LPAREN:
		case NUMBER:
		case URIREF:
		case UVAR:
		{
			verb();
			v1_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			objectList(subj, v1_AST);
			astFactory.addASTChild(currentAST, returnAST);
			propValue_AST = (AST)currentAST.root;
			break;
		}
		case KW_IS:
		{
			verbReverse();
			v2_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			subjectList(subj, v2_AST);
			astFactory.addASTChild(currentAST, returnAST);
			propValue_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = propValue_AST;
	}
	
	public final void verb() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST verb_AST = null;
		
		switch ( LA(1)) {
		case QNAME:
		case KW_THIS:
		case STRING:
		case LBRACK:
		case LPAREN:
		case NUMBER:
		case URIREF:
		case UVAR:
		{
			item();
			astFactory.addASTChild(currentAST, returnAST);
			verb_AST = (AST)currentAST.root;
			break;
		}
		case KW_A:
		{
			kwA();
			astFactory.addASTChild(currentAST, returnAST);
			verb_AST = (AST)currentAST.root;
			break;
		}
		case EQUAL:
		{
			AST tmp12_AST = null;
			tmp12_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp12_AST);
			match(EQUAL);
			verb_AST = (AST)currentAST.root;
			break;
		}
		case ARROW_R:
		{
			AST tmp13_AST = null;
			tmp13_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp13_AST);
			match(ARROW_R);
			verb_AST = (AST)currentAST.root;
			break;
		}
		case ARROW_L:
		{
			AST tmp14_AST = null;
			tmp14_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp14_AST);
			match(ARROW_L);
			verb_AST = (AST)currentAST.root;
			break;
		}
		case ARROW_PATH_L:
		{
			match(ARROW_PATH_L);
			node();
			astFactory.addASTChild(currentAST, returnAST);
			match(ARROW_PATH_R);
			verb_AST = (AST)currentAST.root;
			break;
		}
		case KW_HAS:
		{
			kwHAS();
			item();
			astFactory.addASTChild(currentAST, returnAST);
			verb_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = verb_AST;
	}
	
	public final void objectList(
		AST subj, AST prop
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST objectList_AST = null;
		AST obj_AST = null;
		
		switch ( LA(1)) {
		case QNAME:
		case KW_THIS:
		case STRING:
		case LBRACK:
		case LPAREN:
		case NUMBER:
		case URIREF:
		case UVAR:
		{
			item();
			obj_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				emitQuad(subj,prop,obj_AST) ;
			}
			{
			switch ( LA(1)) {
			case COMMA:
			{
				AST tmp17_AST = null;
				tmp17_AST = astFactory.create(LT(1));
				match(COMMA);
				objectList(subj, prop);
				break;
			}
			case SEP:
			case RCURLY:
			case SEMI:
			case RBRACK:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case SEP:
		case RCURLY:
		case SEMI:
		case RBRACK:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = objectList_AST;
	}
	
	public final void verbReverse() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST verbReverse_AST = null;
		AST n_AST = null;
		
		kwIS();
		node();
		n_AST = (AST)returnAST;
		astFactory.addASTChild(currentAST, returnAST);
		kwOF();
		verbReverse_AST = (AST)currentAST.root;
		returnAST = verbReverse_AST;
	}
	
	public final void subjectList(
		AST oldSub, AST prop
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST subjectList_AST = null;
		AST obj_AST = null;
		
		item();
		obj_AST = (AST)returnAST;
		if ( inputState.guessing==0 ) {
			emitQuad(obj_AST, prop, oldSub) ;
		}
		{
		switch ( LA(1)) {
		case COMMA:
		{
			AST tmp18_AST = null;
			tmp18_AST = astFactory.create(LT(1));
			match(COMMA);
			subjectList(oldSub, prop);
			break;
		}
		case SEP:
		case RCURLY:
		case SEMI:
		case RBRACK:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		returnAST = subjectList_AST;
	}
	
	public final void node() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST node_AST = null;
		
		switch ( LA(1)) {
		case QNAME:
		{
			qname();
			astFactory.addASTChild(currentAST, returnAST);
			node_AST = (AST)currentAST.root;
			break;
		}
		case URIREF:
		{
			uriref();
			astFactory.addASTChild(currentAST, returnAST);
			node_AST = (AST)currentAST.root;
			break;
		}
		case LBRACK:
		case LPAREN:
		{
			anonnode(null);
			astFactory.addASTChild(currentAST, returnAST);
			node_AST = (AST)currentAST.root;
			break;
		}
		case STRING:
		case NUMBER:
		{
			literal();
			astFactory.addASTChild(currentAST, returnAST);
			node_AST = (AST)currentAST.root;
			break;
		}
		case KW_THIS:
		{
			kwTHIS();
			astFactory.addASTChild(currentAST, returnAST);
			node_AST = (AST)currentAST.root;
			break;
		}
		case UVAR:
		{
			variableDT();
			astFactory.addASTChild(currentAST, returnAST);
			node_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = node_AST;
	}
	
	public final void testPoint() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST testPoint_AST = null;
		AST v_AST = null;
		
		verb();
		v_AST = (AST)returnAST;
		if ( inputState.guessing==0 ) {
			AntlrUtils.ast(System.out, v_AST) ;
		}
		returnAST = testPoint_AST;
	}
	
	public final void anonnode(
		AST label
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST anonnode_AST = null;
		String oldCxt = null ; String cxt = null ;
		
		switch ( LA(1)) {
		case LBRACK:
		{
			match(LBRACK);
			if ( inputState.guessing==0 ) {
				anonnode_AST = (AST)currentAST.root;
				if ( label == null )
					          label = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(ANON,genAnonId()))) ;
						  anonnode_AST = label ;
						
				currentAST.root = anonnode_AST;
				currentAST.child = anonnode_AST!=null &&anonnode_AST.getFirstChild()!=null ?
					anonnode_AST.getFirstChild() : anonnode_AST;
				currentAST.advanceChildToEnd();
			}
			propertyList(label);
			astFactory.addASTChild(currentAST, returnAST);
			match(RBRACK);
			anonnode_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			list(label);
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			anonnode_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = anonnode_AST;
	}
	
	public final void literal() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST literal_AST = null;
		Token  n = null;
		AST n_AST = null;
		Token  s = null;
		AST s_AST = null;
		
		switch ( LA(1)) {
		case NUMBER:
		{
			n = LT(1);
			n_AST = astFactory.create(n);
			astFactory.addASTChild(currentAST, n_AST);
			match(NUMBER);
			literal_AST = (AST)currentAST.root;
			break;
		}
		case STRING:
		{
			s = LT(1);
			s_AST = astFactory.create(s);
			astFactory.addASTChild(currentAST, s_AST);
			match(STRING);
			literalModifier();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				literal_AST = (AST)currentAST.root;
				literal_AST.setType(LITERAL) ;
			}
			literal_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = literal_AST;
	}
	
	public final void kwTHIS() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST kwTHIS_AST = null;
		
		AST tmp23_AST = null;
		tmp23_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp23_AST);
		match(KW_THIS);
		kwTHIS_AST = (AST)currentAST.root;
		returnAST = kwTHIS_AST;
	}
	
	public final void variableDT() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variableDT_AST = null;
		Token  v = null;
		AST v_AST = null;
		AST dt_AST = null;
		
		v = LT(1);
		v_AST = astFactory.create(v);
		astFactory.addASTChild(currentAST, v_AST);
		match(UVAR);
		{
		switch ( LA(1)) {
		case DATATYPE:
		{
			AST tmp24_AST = null;
			tmp24_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp24_AST);
			match(DATATYPE);
			datatype();
			dt_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case EOF:
		case QNAME:
		case SEP:
		case KW_THIS:
		case KW_OF:
		case KW_HAS:
		case KW_A:
		case KW_IS:
		case STRING:
		case RCURLY:
		case SEMI:
		case COMMA:
		case PATH:
		case RPATH:
		case EQUAL:
		case ARROW_R:
		case ARROW_L:
		case ARROW_PATH_L:
		case ARROW_PATH_R:
		case LBRACK:
		case RBRACK:
		case LPAREN:
		case RPAREN:
		case NUMBER:
		case URIREF:
		case UVAR:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			variableDT_AST = (AST)currentAST.root;
			variableDT_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(UVAR,v.getText())).add(dt_AST)) ;
			currentAST.root = variableDT_AST;
			currentAST.child = variableDT_AST!=null &&variableDT_AST.getFirstChild()!=null ?
				variableDT_AST.getFirstChild() : variableDT_AST;
			currentAST.advanceChildToEnd();
		}
		variableDT_AST = (AST)currentAST.root;
		returnAST = variableDT_AST;
	}
	
	public final void kwOF() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST kwOF_AST = null;
		
		AST tmp25_AST = null;
		tmp25_AST = astFactory.create(LT(1));
		match(KW_OF);
		returnAST = kwOF_AST;
	}
	
	public final void kwHAS() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST kwHAS_AST = null;
		
		AST tmp26_AST = null;
		tmp26_AST = astFactory.create(LT(1));
		match(KW_HAS);
		returnAST = kwHAS_AST;
	}
	
	public final void kwA() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST kwA_AST = null;
		
		AST tmp27_AST = null;
		tmp27_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp27_AST);
		match(KW_A);
		kwA_AST = (AST)currentAST.root;
		returnAST = kwA_AST;
	}
	
	public final void kwIS() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST kwIS_AST = null;
		
		AST tmp28_AST = null;
		tmp28_AST = astFactory.create(LT(1));
		match(KW_IS);
		returnAST = kwIS_AST;
	}
	
	public final void list(
		AST label
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST list_AST = null;
		AST i_AST = null;
		AST n_AST = null;
		
		switch ( LA(1)) {
		case QNAME:
		case KW_THIS:
		case STRING:
		case LBRACK:
		case LPAREN:
		case NUMBER:
		case URIREF:
		case UVAR:
		{
			item();
			i_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				list_AST = (AST)currentAST.root;
				
					  	if ( label == null )
					          label = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(ANON,genAnonId()))) ;
						list_AST = label ;
					
				currentAST.root = list_AST;
				currentAST.child = list_AST!=null &&list_AST.getFirstChild()!=null ?
					list_AST.getFirstChild() : list_AST;
				currentAST.advanceChildToEnd();
			}
			list(null);
			n_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				
					  	if ( emitListTypeQuad )
					  	    emitQuad(label, (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(KW_A,"list"))), (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(TK_LIST,"List"))) );
					    emitQuad(label,  (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(TK_LIST_FIRST,"first"))),   i_AST);
						emitQuad(label,  (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(TK_LIST_REST,"rest"))),     n_AST) ;
					
			}
			list_AST = (AST)currentAST.root;
			break;
		}
		case RPAREN:
		{
			if ( inputState.guessing==0 ) {
				list_AST = (AST)currentAST.root;
				list_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(TK_LIST_NIL,"nil")));
				currentAST.root = list_AST;
				currentAST.child = list_AST!=null &&list_AST.getFirstChild()!=null ?
					list_AST.getFirstChild() : list_AST;
				currentAST.advanceChildToEnd();
			}
			list_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = list_AST;
	}
	
	public final void literalModifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST literalModifier_AST = null;
		
		literalModifier1();
		astFactory.addASTChild(currentAST, returnAST);
		literalModifier1();
		astFactory.addASTChild(currentAST, returnAST);
		literalModifier_AST = (AST)currentAST.root;
		returnAST = literalModifier_AST;
	}
	
	public final void literalModifier1() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST literalModifier1_AST = null;
		AST dt_AST = null;
		
		boolean synPredMatched39 = false;
		if (((LA(1)==AT_LANG))) {
			int _m39 = mark();
			synPredMatched39 = true;
			inputState.guessing++;
			try {
				{
				match(AT_LANG);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched39 = false;
			}
			rewind(_m39);
			inputState.guessing--;
		}
		if ( synPredMatched39 ) {
			AST tmp29_AST = null;
			tmp29_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp29_AST);
			match(AT_LANG);
			literalModifier1_AST = (AST)currentAST.root;
		}
		else {
			boolean synPredMatched41 = false;
			if (((LA(1)==DATATYPE))) {
				int _m41 = mark();
				synPredMatched41 = true;
				inputState.guessing++;
				try {
					{
					match(DATATYPE);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched41 = false;
				}
				rewind(_m41);
				inputState.guessing--;
			}
			if ( synPredMatched41 ) {
				AST tmp30_AST = null;
				tmp30_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp30_AST);
				match(DATATYPE);
				datatype();
				dt_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					literalModifier1_AST = (AST)currentAST.root;
					literalModifier1_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(tmp30_AST)).add(dt_AST)) ;
					currentAST.root = literalModifier1_AST;
					currentAST.child = literalModifier1_AST!=null &&literalModifier1_AST.getFirstChild()!=null ?
						literalModifier1_AST.getFirstChild() : literalModifier1_AST;
					currentAST.advanceChildToEnd();
				}
				literalModifier1_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_0.member(LA(1)))) {
				literalModifier1_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			returnAST = literalModifier1_AST;
		}
		
	public final void datatype() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST datatype_AST = null;
		
		switch ( LA(1)) {
		case QNAME:
		{
			qname();
			astFactory.addASTChild(currentAST, returnAST);
			datatype_AST = (AST)currentAST.root;
			break;
		}
		case URIREF:
		{
			uriref();
			astFactory.addASTChild(currentAST, returnAST);
			datatype_AST = (AST)currentAST.root;
			break;
		}
		case UVAR:
		{
			variableNoDT();
			astFactory.addASTChild(currentAST, returnAST);
			datatype_AST = (AST)currentAST.root;
			break;
		}
		case STRING:
		case NUMBER:
		{
			literal();
			astFactory.addASTChild(currentAST, returnAST);
			datatype_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = datatype_AST;
	}
	
	public final void variableNoDT() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variableNoDT_AST = null;
		Token  v = null;
		AST v_AST = null;
		
		v = LT(1);
		v_AST = astFactory.create(v);
		astFactory.addASTChild(currentAST, v_AST);
		match(UVAR);
		variableNoDT_AST = (AST)currentAST.root;
		returnAST = variableNoDT_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"ANON",
		"FORMULA",
		"QNAME",
		"SEP",
		"KEYWORD",
		"NAME_OP",
		"KW_THIS",
		"KW_OF",
		"KW_HAS",
		"KW_A",
		"KW_IS",
		"TK_LIST",
		"TK_LIST_FIRST",
		"TK_LIST_REST",
		"TK_LIST_NIL",
		"AT_PREFIX",
		"AT_LANG",
		"STRING",
		"LITERAL",
		"LCURLY",
		"RCURLY",
		"SEMI",
		"COMMA",
		"PATH",
		"RPATH",
		"EQUAL",
		"ARROW_R",
		"ARROW_L",
		"ARROW_PATH_L",
		"ARROW_PATH_R",
		"LBRACK",
		"RBRACK",
		"LPAREN",
		"RPAREN",
		"NUMBER",
		"DATATYPE",
		"URIREF",
		"UVAR",
		"THING",
		"URI_OR_IMPLIES",
		"URICHAR",
		"AT_WORD",
		"XNAMECHAR",
		"XNAME",
		"NSNAME",
		"LNAME",
		"SEP_OR_PATH",
		"DOT",
		"AT",
		"LANGLE",
		"RANGLE",
		"NAME_IT",
		"QUESTION",
		"ARROW_MEANS",
		"COLON",
		"SL_COMMENT",
		"NL1",
		"NL2",
		"NL3",
		"NL",
		"WS",
		"NWS",
		"ALPHA",
		"NUMERIC",
		"ALPHANUMERIC",
		"NON_ANC",
		"STRING1",
		"STRING2",
		"QUOTE3S",
		"QUOTE3D",
		"ESCAPE",
		"ESC_CHAR",
		"HEX_DIGIT",
		"HEX4"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 4398032911554L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	
	}
