// $ANTLR 2.7.2: "trig.g" -> "TriGAntlrLexer.java"$

package de.fuberlin.wiwiss.ng4j.trig.parser ;
import de.fuberlin.wiwiss.ng4j.trig.AntlrUtils ;
import de.fuberlin.wiwiss.ng4j.trig.TriGParserEventHandler ;
import java.io.* ;
import antlr.TokenStreamRecognitionException ;

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

public class TriGAntlrLexer extends antlr.CharScanner implements TriGAntlrParserTokenTypes, TokenStream
 {
public TriGAntlrLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public TriGAntlrLexer(Reader in) {
	this(new CharBuffer(in));
}
public TriGAntlrLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public TriGAntlrLexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = true;
	setCaseSensitive(true);
	literals = new Hashtable();
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case '?':
				{
					mUVAR(true);
					theRetToken=_returnToken;
					break;
				}
				case '"':  case '\'':
				{
					mSTRING(true);
					theRetToken=_returnToken;
					break;
				}
				case '.':
				{
					mSEP_OR_PATH(true);
					theRetToken=_returnToken;
					break;
				}
				case '(':
				{
					mLPAREN(true);
					theRetToken=_returnToken;
					break;
				}
				case ')':
				{
					mRPAREN(true);
					theRetToken=_returnToken;
					break;
				}
				case '[':
				{
					mLBRACK(true);
					theRetToken=_returnToken;
					break;
				}
				case ']':
				{
					mRBRACK(true);
					theRetToken=_returnToken;
					break;
				}
				case '{':
				{
					mLCURLY(true);
					theRetToken=_returnToken;
					break;
				}
				case '}':
				{
					mRCURLY(true);
					theRetToken=_returnToken;
					break;
				}
				case ';':
				{
					mSEMI(true);
					theRetToken=_returnToken;
					break;
				}
				case ',':
				{
					mCOMMA(true);
					theRetToken=_returnToken;
					break;
				}
				case '!':
				{
					mPATH(true);
					theRetToken=_returnToken;
					break;
				}
				case '#':
				{
					mSL_COMMENT(true);
					theRetToken=_returnToken;
					break;
				}
				case '\t':  case '\n':  case '\u000c':  case '\r':
				case ' ':
				{
					mWS(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if ((LA(1)=='<') && (_tokenSet_0.member(LA(2)))) {
						mURI_OR_IMPLIES(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='@') && (_tokenSet_1.member(LA(2)))) {
						mAT_WORD(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='^') && (LA(2)=='^')) {
						mDATATYPE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='=') && (LA(2)=='>')) {
						mARROW_R(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='>') && (LA(2)=='-')) {
						mARROW_PATH_L(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='-') && (LA(2)=='>')) {
						mARROW_PATH_R(true);
						theRetToken=_returnToken;
					}
					else if ((_tokenSet_2.member(LA(1))) && (true)) {
						mTHING(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='<') && (true)) {
						mLANGLE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='>') && (true)) {
						mRANGLE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='@') && (true)) {
						mAT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='^') && (true)) {
						mRPATH(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='=') && (true)) {
						mEQUAL(true);
						theRetToken=_returnToken;
					}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_ttype = testLiteralsTable(_ttype);
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mTHING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = THING;
		int _saveIndex;
		
		boolean synPredMatched62 = false;
		if (((LA(1)=='h') && (LA(2)=='a') && (LA(3)=='s'))) {
			int _m62 = mark();
			synPredMatched62 = true;
			inputState.guessing++;
			try {
				{
				match("has");
				mNON_ANC(false);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched62 = false;
			}
			rewind(_m62);
			inputState.guessing--;
		}
		if ( synPredMatched62 ) {
			match("has");
			if ( inputState.guessing==0 ) {
				_ttype = KW_HAS ;
			}
		}
		else {
			boolean synPredMatched66 = false;
			if (((LA(1)=='t') && (LA(2)=='h') && (LA(3)=='i'))) {
				int _m66 = mark();
				synPredMatched66 = true;
				inputState.guessing++;
				try {
					{
					match("this");
					mNON_ANC(false);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched66 = false;
				}
				rewind(_m66);
				inputState.guessing--;
			}
			if ( synPredMatched66 ) {
				match("this");
				if ( inputState.guessing==0 ) {
					_ttype = KW_THIS ;
				}
			}
			else {
				boolean synPredMatched58 = false;
				if (((LA(1)==':') && (LA(2)=='-') && (true))) {
					int _m58 = mark();
					synPredMatched58 = true;
					inputState.guessing++;
					try {
						{
						mCOLON(false);
						match('-');
						}
					}
					catch (RecognitionException pe) {
						synPredMatched58 = false;
					}
					rewind(_m58);
					inputState.guessing--;
				}
				if ( synPredMatched58 ) {
					match(":-");
					if ( inputState.guessing==0 ) {
						_ttype = NAME_OP ;
					}
				}
				else {
					boolean synPredMatched64 = false;
					if (((LA(1)=='o') && (LA(2)=='f') && (true))) {
						int _m64 = mark();
						synPredMatched64 = true;
						inputState.guessing++;
						try {
							{
							match("of");
							mNON_ANC(false);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched64 = false;
						}
						rewind(_m64);
						inputState.guessing--;
					}
					if ( synPredMatched64 ) {
						match("of");
						if ( inputState.guessing==0 ) {
							_ttype = KW_OF ;
						}
					}
					else {
						boolean synPredMatched70 = false;
						if (((LA(1)=='i') && (LA(2)=='s') && (true))) {
							int _m70 = mark();
							synPredMatched70 = true;
							inputState.guessing++;
							try {
								{
								match("is");
								mNON_ANC(false);
								}
							}
							catch (RecognitionException pe) {
								synPredMatched70 = false;
							}
							rewind(_m70);
							inputState.guessing--;
						}
						if ( synPredMatched70 ) {
							match("is");
							if ( inputState.guessing==0 ) {
								_ttype = KW_IS ;
							}
						}
						else {
							boolean synPredMatched50 = false;
							if (((_tokenSet_3.member(LA(1))) && (true) && (true))) {
								int _m50 = mark();
								synPredMatched50 = true;
								inputState.guessing++;
								try {
									{
									mNSNAME(false);
									mCOLON(false);
									mLNAME(false);
									}
								}
								catch (RecognitionException pe) {
									synPredMatched50 = false;
								}
								rewind(_m50);
								inputState.guessing--;
							}
							if ( synPredMatched50 ) {
								mNSNAME(false);
								mCOLON(false);
								mLNAME(false);
								if ( inputState.guessing==0 ) {
									_ttype = QNAME ;
								}
							}
							else {
								boolean synPredMatched52 = false;
								if (((LA(1)==':') && (true) && (true))) {
									int _m52 = mark();
									synPredMatched52 = true;
									inputState.guessing++;
									try {
										{
										mCOLON(false);
										mLNAME(false);
										}
									}
									catch (RecognitionException pe) {
										synPredMatched52 = false;
									}
									rewind(_m52);
									inputState.guessing--;
								}
								if ( synPredMatched52 ) {
									mCOLON(false);
									mLNAME(false);
									if ( inputState.guessing==0 ) {
										_ttype = QNAME ;
									}
								}
								else {
									boolean synPredMatched54 = false;
									if (((_tokenSet_3.member(LA(1))) && (true) && (true))) {
										int _m54 = mark();
										synPredMatched54 = true;
										inputState.guessing++;
										try {
											{
											mNSNAME(false);
											mCOLON(false);
											}
										}
										catch (RecognitionException pe) {
											synPredMatched54 = false;
										}
										rewind(_m54);
										inputState.guessing--;
									}
									if ( synPredMatched54 ) {
										mNSNAME(false);
										mCOLON(false);
										if ( inputState.guessing==0 ) {
											_ttype = QNAME ;
										}
									}
									else {
										boolean synPredMatched56 = false;
										if (((LA(1)==':') && (true) && (true))) {
											int _m56 = mark();
											synPredMatched56 = true;
											inputState.guessing++;
											try {
												{
												mCOLON(false);
												}
											}
											catch (RecognitionException pe) {
												synPredMatched56 = false;
											}
											rewind(_m56);
											inputState.guessing--;
										}
										if ( synPredMatched56 ) {
											mCOLON(false);
											if ( inputState.guessing==0 ) {
												_ttype = QNAME ;
											}
										}
										else {
											boolean synPredMatched60 = false;
											if (((_tokenSet_4.member(LA(1))) && (true) && (true))) {
												int _m60 = mark();
												synPredMatched60 = true;
												inputState.guessing++;
												try {
													{
													mNUMBER(false);
													}
												}
												catch (RecognitionException pe) {
													synPredMatched60 = false;
												}
												rewind(_m60);
												inputState.guessing--;
											}
											if ( synPredMatched60 ) {
												mNUMBER(false);
												if ( inputState.guessing==0 ) {
													_ttype = NUMBER ;
												}
											}
											else {
												boolean synPredMatched68 = false;
												if (((LA(1)=='a') && (true) && (true))) {
													int _m68 = mark();
													synPredMatched68 = true;
													inputState.guessing++;
													try {
														{
														match("a");
														mNON_ANC(false);
														}
													}
													catch (RecognitionException pe) {
														synPredMatched68 = false;
													}
													rewind(_m68);
													inputState.guessing--;
												}
												if ( synPredMatched68 ) {
													match("a");
													if ( inputState.guessing==0 ) {
														_ttype = KW_A ;
													}
												}
												else {
													throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
												}
												}}}}}}}}}}
												if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
													_token = makeToken(_ttype);
													_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
												}
												_returnToken = _token;
											}
											
	protected final void mNSNAME(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NSNAME;
		int _saveIndex;
		
		mXNAME(false);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCOLON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COLON;
		int _saveIndex;
		
		match(':');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mLNAME(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LNAME;
		int _saveIndex;
		
		mXNAME(false);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNUMBER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NUMBER;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case '+':
		{
			match('+');
			break;
		}
		case '-':
		{
			match('-');
			break;
		}
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		{
		int _cnt118=0;
		_loop118:
		do {
			if (((LA(1) >= '0' && LA(1) <= '9'))) {
				matchRange('0','9');
			}
			else {
				if ( _cnt118>=1 ) { break _loop118; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt118++;
		} while (true);
		}
		{
		boolean synPredMatched122 = false;
		if (((LA(1)=='.'))) {
			int _m122 = mark();
			synPredMatched122 = true;
			inputState.guessing++;
			try {
				{
				mDOT(false);
				{
				matchRange('0','9');
				}
				}
			}
			catch (RecognitionException pe) {
				synPredMatched122 = false;
			}
			rewind(_m122);
			inputState.guessing--;
		}
		if ( synPredMatched122 ) {
			mDOT(false);
			{
			int _cnt124=0;
			_loop124:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					matchRange('0','9');
				}
				else {
					if ( _cnt124>=1 ) { break _loop124; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt124++;
			} while (true);
			}
		}
		else {
		}
		
		}
		{
		if ((LA(1)=='E'||LA(1)=='e')) {
			{
			switch ( LA(1)) {
			case 'e':
			{
				match('e');
				break;
			}
			case 'E':
			{
				match('E');
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			}
			{
			switch ( LA(1)) {
			case '+':
			{
				match('+');
				break;
			}
			case '-':
			{
				match('-');
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			}
			{
			int _cnt129=0;
			_loop129:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					matchRange('0','9');
				}
				else {
					if ( _cnt129>=1 ) { break _loop129; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt129++;
			} while (true);
			}
		}
		else {
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNON_ANC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NON_ANC;
		int _saveIndex;
		
		{
		match(_tokenSet_5);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mURI_OR_IMPLIES(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = URI_OR_IMPLIES;
		int _saveIndex;
		
		boolean synPredMatched75 = false;
		if (((LA(1)=='<') && (LA(2)=='=') && (LA(3)=='>'))) {
			int _m75 = mark();
			synPredMatched75 = true;
			inputState.guessing++;
			try {
				{
				mARROW_MEANS(false);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched75 = false;
			}
			rewind(_m75);
			inputState.guessing--;
		}
		if ( synPredMatched75 ) {
			mARROW_MEANS(false);
			if ( inputState.guessing==0 ) {
				_ttype = ARROW_MEANS ;
			}
		}
		else {
			boolean synPredMatched73 = false;
			if (((LA(1)=='<') && (LA(2)=='=') && (true))) {
				int _m73 = mark();
				synPredMatched73 = true;
				inputState.guessing++;
				try {
					{
					mARROW_L(false);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched73 = false;
				}
				rewind(_m73);
				inputState.guessing--;
			}
			if ( synPredMatched73 ) {
				mARROW_L(false);
				if ( inputState.guessing==0 ) {
					_ttype = ARROW_L ;
				}
			}
			else if ((LA(1)=='<') && (_tokenSet_0.member(LA(2))) && (true)) {
				mURIREF(false);
				if ( inputState.guessing==0 ) {
					_ttype = URIREF ;
				}
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		}
		
	protected final void mARROW_L(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ARROW_L;
		int _saveIndex;
		
		match("<=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mARROW_MEANS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ARROW_MEANS;
		int _saveIndex;
		
		match("<=>");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mURIREF(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = URIREF;
		int _saveIndex;
		
		_saveIndex=text.length();
		mLANGLE(false);
		text.setLength(_saveIndex);
		{
		_loop79:
		do {
			// nongreedy exit test
			if ((LA(1)=='>') && (true)) break _loop79;
			if ((_tokenSet_0.member(LA(1))) && (_tokenSet_0.member(LA(2)))) {
				{
				match(_tokenSet_0);
				}
			}
			else {
				break _loop79;
			}
			
		} while (true);
		}
		_saveIndex=text.length();
		mRANGLE(false);
		text.setLength(_saveIndex);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLANGLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LANGLE;
		int _saveIndex;
		
		match('<');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRANGLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RANGLE;
		int _saveIndex;
		
		match('>');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mURICHAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = URICHAR;
		int _saveIndex;
		
		switch ( LA(1)) {
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':  case 'A':  case 'B':
		case 'C':  case 'D':  case 'E':  case 'F':
		case 'G':  case 'H':  case 'I':  case 'J':
		case 'K':  case 'L':  case 'M':  case 'N':
		case 'O':  case 'P':  case 'Q':  case 'R':
		case 'S':  case 'T':  case 'U':  case 'V':
		case 'W':  case 'X':  case 'Y':  case 'Z':
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':  case 'g':  case 'h':
		case 'i':  case 'j':  case 'k':  case 'l':
		case 'm':  case 'n':  case 'o':  case 'p':
		case 'q':  case 'r':  case 's':  case 't':
		case 'u':  case 'v':  case 'w':  case 'x':
		case 'y':  case 'z':
		{
			mALPHANUMERIC(false);
			break;
		}
		case '-':
		{
			match('-');
			break;
		}
		case '_':
		{
			match('_');
			break;
		}
		case '.':
		{
			match('.');
			break;
		}
		case '!':
		{
			match('!');
			break;
		}
		case '~':
		{
			match('~');
			break;
		}
		case '*':
		{
			match('*');
			break;
		}
		case '\'':
		{
			match("'");
			break;
		}
		case '(':
		{
			match('(');
			break;
		}
		case ')':
		{
			match(')');
			break;
		}
		case ';':
		{
			match(';');
			break;
		}
		case '/':
		{
			match('/');
			break;
		}
		case '?':
		{
			match('?');
			break;
		}
		case ':':
		{
			match(':');
			break;
		}
		case '@':
		{
			match('@');
			break;
		}
		case '&':
		{
			match('&');
			break;
		}
		case '=':
		{
			match('=');
			break;
		}
		case '+':
		{
			match('+');
			break;
		}
		case '$':
		{
			match('$');
			break;
		}
		case ',':
		{
			match(',');
			break;
		}
		case '{':
		{
			match('{');
			break;
		}
		case '}':
		{
			match('}');
			break;
		}
		case '|':
		{
			match('|');
			break;
		}
		case '\\':
		{
			match('\\');
			break;
		}
		case '^':
		{
			match('^');
			break;
		}
		case '[':
		{
			match('[');
			break;
		}
		case ']':
		{
			match(']');
			break;
		}
		case '`':
		{
			match('`');
			break;
		}
		case '%':
		{
			match('%');
			break;
		}
		case '#':
		{
			match('#');
			break;
		}
		case '"':
		{
			match('"');
			break;
		}
		case ' ':
		{
			match(' ');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mALPHANUMERIC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ALPHANUMERIC;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case 'A':  case 'B':  case 'C':  case 'D':
		case 'E':  case 'F':  case 'G':  case 'H':
		case 'I':  case 'J':  case 'K':  case 'L':
		case 'M':  case 'N':  case 'O':  case 'P':
		case 'Q':  case 'R':  case 'S':  case 'T':
		case 'U':  case 'V':  case 'W':  case 'X':
		case 'Y':  case 'Z':  case 'a':  case 'b':
		case 'c':  case 'd':  case 'e':  case 'f':
		case 'g':  case 'h':  case 'i':  case 'j':
		case 'k':  case 'l':  case 'm':  case 'n':
		case 'o':  case 'p':  case 'q':  case 'r':
		case 's':  case 't':  case 'u':  case 'v':
		case 'w':  case 'x':  case 'y':  case 'z':
		{
			mALPHA(false);
			break;
		}
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			mNUMERIC(false);
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mUVAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = UVAR;
		int _saveIndex;
		
		mQUESTION(false);
		{
		int _cnt83=0;
		_loop83:
		do {
			if ((_tokenSet_6.member(LA(1)))) {
				mALPHANUMERIC(false);
			}
			else {
				if ( _cnt83>=1 ) { break _loop83; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt83++;
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mQUESTION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = QUESTION;
		int _saveIndex;
		
		match('?');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mAT_WORD(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = AT_WORD;
		int _saveIndex;
		Token a=null;
		
		boolean synPredMatched86 = false;
		if (((LA(1)=='@') && (LA(2)=='p') && (LA(3)=='r'))) {
			int _m86 = mark();
			synPredMatched86 = true;
			inputState.guessing++;
			try {
				{
				mAT(false);
				match("prefix");
				}
			}
			catch (RecognitionException pe) {
				synPredMatched86 = false;
			}
			rewind(_m86);
			inputState.guessing--;
		}
		if ( synPredMatched86 ) {
			mAT(false);
			match("prefix");
			if ( inputState.guessing==0 ) {
				_ttype = AT_PREFIX ;
			}
		}
		else {
			boolean synPredMatched89 = false;
			if (((LA(1)=='@') && (_tokenSet_1.member(LA(2))) && (true))) {
				int _m89 = mark();
				synPredMatched89 = true;
				inputState.guessing++;
				try {
					{
					mAT(false);
					{
					mALPHA(false);
					}
					}
				}
				catch (RecognitionException pe) {
					synPredMatched89 = false;
				}
				rewind(_m89);
				inputState.guessing--;
			}
			if ( synPredMatched89 ) {
				mAT(false);
				{
				int _cnt_a=0;
				a:
				do {
					if ((_tokenSet_1.member(LA(1)))) {
						mALPHA(false);
					}
					else {
						if ( _cnt_a>=1 ) { break a; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
					}
					
					_cnt_a++;
				} while (true);
				}
				{
				if ((LA(1)=='-')) {
					match("-");
					{
					_loop94:
					do {
						if ((_tokenSet_1.member(LA(1)))) {
							mALPHA(false);
						}
						else {
							break _loop94;
						}
						
					} while (true);
					}
				}
				else {
				}
				
				}
				if ( inputState.guessing==0 ) {
					_ttype = AT_LANG ;
				}
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
				_token = makeToken(_ttype);
				_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
			}
			_returnToken = _token;
		}
		
	public final void mAT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = AT;
		int _saveIndex;
		
		match('@');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mALPHA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ALPHA;
		int _saveIndex;
		
		switch ( LA(1)) {
		case 'A':  case 'B':  case 'C':  case 'D':
		case 'E':  case 'F':  case 'G':  case 'H':
		case 'I':  case 'J':  case 'K':  case 'L':
		case 'M':  case 'N':  case 'O':  case 'P':
		case 'Q':  case 'R':  case 'S':  case 'T':
		case 'U':  case 'V':  case 'W':  case 'X':
		case 'Y':  case 'Z':
		{
			{
			matchRange('A','Z');
			}
			break;
		}
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':  case 'g':  case 'h':
		case 'i':  case 'j':  case 'k':  case 'l':
		case 'm':  case 'n':  case 'o':  case 'p':
		case 'q':  case 'r':  case 's':  case 't':
		case 'u':  case 'v':  case 'w':  case 'x':
		case 'y':  case 'z':
		{
			{
			matchRange('a','z');
			}
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mXNAMECHAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = XNAMECHAR;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case 'A':  case 'B':  case 'C':  case 'D':
		case 'E':  case 'F':  case 'G':  case 'H':
		case 'I':  case 'J':  case 'K':  case 'L':
		case 'M':  case 'N':  case 'O':  case 'P':
		case 'Q':  case 'R':  case 'S':  case 'T':
		case 'U':  case 'V':  case 'W':  case 'X':
		case 'Y':  case 'Z':
		{
			{
			matchRange('A','Z');
			}
			break;
		}
		case '_':
		{
			match('_');
			break;
		}
		case '-':
		{
			match('-');
			break;
		}
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':  case 'g':  case 'h':
		case 'i':  case 'j':  case 'k':  case 'l':
		case 'm':  case 'n':  case 'o':  case 'p':
		case 'q':  case 'r':  case 's':  case 't':
		case 'u':  case 'v':  case 'w':  case 'x':
		case 'y':  case 'z':
		{
			{
			matchRange('a','z');
			}
			break;
		}
		case '\u0370':  case '\u0371':  case '\u0372':  case '\u0373':
		case '\u0374':  case '\u0375':  case '\u0376':  case '\u0377':
		case '\u0378':  case '\u0379':  case '\u037a':  case '\u037b':
		case '\u037c':  case '\u037d':
		{
			{
			matchRange('\u0370','\u037D');
			}
			break;
		}
		case '\u200c':  case '\u200d':
		{
			{
			matchRange('\u200C','\u200D');
			}
			break;
		}
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			{
			matchRange('0','9');
			}
			break;
		}
		case '\u0300':  case '\u0301':  case '\u0302':  case '\u0303':
		case '\u0304':  case '\u0305':  case '\u0306':  case '\u0307':
		case '\u0308':  case '\u0309':  case '\u030a':  case '\u030b':
		case '\u030c':  case '\u030d':  case '\u030e':  case '\u030f':
		case '\u0310':  case '\u0311':  case '\u0312':  case '\u0313':
		case '\u0314':  case '\u0315':  case '\u0316':  case '\u0317':
		case '\u0318':  case '\u0319':  case '\u031a':  case '\u031b':
		case '\u031c':  case '\u031d':  case '\u031e':  case '\u031f':
		case '\u0320':  case '\u0321':  case '\u0322':  case '\u0323':
		case '\u0324':  case '\u0325':  case '\u0326':  case '\u0327':
		case '\u0328':  case '\u0329':  case '\u032a':  case '\u032b':
		case '\u032c':  case '\u032d':  case '\u032e':  case '\u032f':
		case '\u0330':  case '\u0331':  case '\u0332':  case '\u0333':
		case '\u0334':  case '\u0335':  case '\u0336':  case '\u0337':
		case '\u0338':  case '\u0339':  case '\u033a':  case '\u033b':
		case '\u033c':  case '\u033d':  case '\u033e':  case '\u033f':
		case '\u0340':  case '\u0341':  case '\u0342':  case '\u0343':
		case '\u0344':  case '\u0345':  case '\u0346':  case '\u0347':
		case '\u0348':  case '\u0349':  case '\u034a':  case '\u034b':
		case '\u034c':  case '\u034d':  case '\u034e':  case '\u034f':
		case '\u0350':  case '\u0351':  case '\u0352':  case '\u0353':
		case '\u0354':  case '\u0355':  case '\u0356':  case '\u0357':
		case '\u0358':  case '\u0359':  case '\u035a':  case '\u035b':
		case '\u035c':  case '\u035d':  case '\u035e':  case '\u035f':
		case '\u0360':  case '\u0361':  case '\u0362':  case '\u0363':
		case '\u0364':  case '\u0365':  case '\u0366':  case '\u0367':
		case '\u0368':  case '\u0369':  case '\u036a':  case '\u036b':
		case '\u036c':  case '\u036d':  case '\u036e':  case '\u036f':
		{
			{
			matchRange('\u0300','\u036F');
			}
			break;
		}
		case '\u203f':  case '\u2040':
		{
			{
			matchRange('\u203F','\u2040');
			}
			break;
		}
		case '\u00b7':
		{
			match('\u00B7');
			break;
		}
		default:
			if (((LA(1) >= '\u00c0' && LA(1) <= '\u02ff'))) {
				{
				matchRange('\u00C0','\u02FF');
				}
			}
			else if (((LA(1) >= '\u037f' && LA(1) <= '\u1fff'))) {
				{
				matchRange('\u037F','\u1FFF');
				}
			}
			else if (((LA(1) >= '\u2070' && LA(1) <= '\u218f'))) {
				{
				matchRange('\u2070','\u218F');
				}
			}
			else if (((LA(1) >= '\u2c00' && LA(1) <= '\u2fef'))) {
				{
				matchRange('\u2C00','\u2FEF');
				}
			}
			else if (((LA(1) >= '\u3001' && LA(1) <= '\ud7ff'))) {
				{
				matchRange('\u3001','\uD7FF');
				}
			}
			else if (((LA(1) >= '\uf900' && LA(1) <= '\ufffe'))) {
				{
				matchRange('\uF900','\uFFFE');
				}
			}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mXNAME(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = XNAME;
		int _saveIndex;
		
		{
		_loop112:
		do {
			if ((_tokenSet_7.member(LA(1)))) {
				mXNAMECHAR(false);
			}
			else {
				break _loop112;
			}
			
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mDOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOT;
		int _saveIndex;
		
		match('.');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSTRING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STRING;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case '\'':
		{
			mSTRING1(false);
			break;
		}
		case '"':
		{
			mSTRING2(false);
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mSTRING1(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STRING1;
		int _saveIndex;
		
		boolean synPredMatched190 = false;
		if (((LA(1)=='\'') && (LA(2)=='\'') && (LA(3)=='\''))) {
			int _m190 = mark();
			synPredMatched190 = true;
			inputState.guessing++;
			try {
				{
				mQUOTE3S(false);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched190 = false;
			}
			rewind(_m190);
			inputState.guessing--;
		}
		if ( synPredMatched190 ) {
			_saveIndex=text.length();
			mQUOTE3S(false);
			text.setLength(_saveIndex);
			{
			_loop195:
			do {
				// nongreedy exit test
				if ((LA(1)=='\'') && (LA(2)=='\'') && (LA(3)=='\'')) break _loop195;
				boolean synPredMatched193 = false;
				if (((LA(1)=='\n'||LA(1)=='\r') && ((LA(2) >= '\u0000' && LA(2) <= '\ufffe')) && ((LA(3) >= '\u0000' && LA(3) <= '\ufffe')))) {
					int _m193 = mark();
					synPredMatched193 = true;
					inputState.guessing++;
					try {
						{
						mNL(false);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched193 = false;
					}
					rewind(_m193);
					inputState.guessing--;
				}
				if ( synPredMatched193 ) {
					mNL(false);
				}
				else if ((_tokenSet_8.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\ufffe')) && ((LA(3) >= '\u0000' && LA(3) <= '\ufffe'))) {
					{
					match(_tokenSet_8);
					}
				}
				else if ((LA(1)=='\\')) {
					mESCAPE(false);
				}
				else {
					break _loop195;
				}
				
			} while (true);
			}
			_saveIndex=text.length();
			mQUOTE3S(false);
			text.setLength(_saveIndex);
		}
		else if ((LA(1)=='\'') && ((LA(2) >= '\u0000' && LA(2) <= '\ufffe')) && (true)) {
			_saveIndex=text.length();
			match('\'');
			text.setLength(_saveIndex);
			{
			_loop197:
			do {
				// nongreedy exit test
				if ((LA(1)=='\'') && (true)) break _loop197;
				if ((_tokenSet_8.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\ufffe'))) {
					matchNot('\\');
				}
				else if ((LA(1)=='\\')) {
					mESCAPE(false);
				}
				else {
					break _loop197;
				}
				
			} while (true);
			}
			_saveIndex=text.length();
			match('\'');
			text.setLength(_saveIndex);
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mSTRING2(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STRING2;
		int _saveIndex;
		
		boolean synPredMatched200 = false;
		if (((LA(1)=='"') && (LA(2)=='"') && (LA(3)=='"'))) {
			int _m200 = mark();
			synPredMatched200 = true;
			inputState.guessing++;
			try {
				{
				mQUOTE3D(false);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched200 = false;
			}
			rewind(_m200);
			inputState.guessing--;
		}
		if ( synPredMatched200 ) {
			_saveIndex=text.length();
			mQUOTE3D(false);
			text.setLength(_saveIndex);
			{
			_loop205:
			do {
				// nongreedy exit test
				if ((LA(1)=='"') && (LA(2)=='"') && (LA(3)=='"')) break _loop205;
				boolean synPredMatched203 = false;
				if (((LA(1)=='\n'||LA(1)=='\r') && ((LA(2) >= '\u0000' && LA(2) <= '\ufffe')) && ((LA(3) >= '\u0000' && LA(3) <= '\ufffe')))) {
					int _m203 = mark();
					synPredMatched203 = true;
					inputState.guessing++;
					try {
						{
						mNL(false);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched203 = false;
					}
					rewind(_m203);
					inputState.guessing--;
				}
				if ( synPredMatched203 ) {
					mNL(false);
				}
				else if ((_tokenSet_8.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\ufffe')) && ((LA(3) >= '\u0000' && LA(3) <= '\ufffe'))) {
					{
					match(_tokenSet_8);
					}
				}
				else if ((LA(1)=='\\')) {
					mESCAPE(false);
				}
				else {
					break _loop205;
				}
				
			} while (true);
			}
			_saveIndex=text.length();
			mQUOTE3D(false);
			text.setLength(_saveIndex);
		}
		else if ((LA(1)=='"') && ((LA(2) >= '\u0000' && LA(2) <= '\ufffe')) && (true)) {
			_saveIndex=text.length();
			match('"');
			text.setLength(_saveIndex);
			{
			_loop207:
			do {
				// nongreedy exit test
				if ((LA(1)=='"') && (true)) break _loop207;
				if ((_tokenSet_8.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\ufffe'))) {
					matchNot('\\');
				}
				else if ((LA(1)=='\\')) {
					mESCAPE(false);
				}
				else {
					break _loop207;
				}
				
			} while (true);
			}
			_saveIndex=text.length();
			match('"');
			text.setLength(_saveIndex);
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSEP_OR_PATH(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SEP_OR_PATH;
		int _saveIndex;
		
		boolean synPredMatched135 = false;
		if (((LA(1)=='.') && (true) && (true))) {
			int _m135 = mark();
			synPredMatched135 = true;
			inputState.guessing++;
			try {
				{
				mDOT(false);
				{
				switch ( LA(1)) {
				case 'A':  case 'B':  case 'C':  case 'D':
				case 'E':  case 'F':  case 'G':  case 'H':
				case 'I':  case 'J':  case 'K':  case 'L':
				case 'M':  case 'N':  case 'O':  case 'P':
				case 'Q':  case 'R':  case 'S':  case 'T':
				case 'U':  case 'V':  case 'W':  case 'X':
				case 'Y':  case 'Z':  case 'a':  case 'b':
				case 'c':  case 'd':  case 'e':  case 'f':
				case 'g':  case 'h':  case 'i':  case 'j':
				case 'k':  case 'l':  case 'm':  case 'n':
				case 'o':  case 'p':  case 'q':  case 'r':
				case 's':  case 't':  case 'u':  case 'v':
				case 'w':  case 'x':  case 'y':  case 'z':
				{
					mALPHA(false);
					break;
				}
				case '_':
				{
					match('_');
					break;
				}
				case ':':
				{
					mCOLON(false);
					break;
				}
				case '<':
				{
					mLANGLE(false);
					break;
				}
				default:
				{
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				}
				}
				}
			}
			catch (RecognitionException pe) {
				synPredMatched135 = false;
			}
			rewind(_m135);
			inputState.guessing--;
		}
		if ( synPredMatched135 ) {
			mDOT(false);
			if ( inputState.guessing==0 ) {
				_ttype = PATH ;
			}
		}
		else if ((LA(1)=='.') && (true) && (true)) {
			mDOT(false);
			if ( inputState.guessing==0 ) {
				_ttype = SEP ;
			}
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LPAREN;
		int _saveIndex;
		
		match('(');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RPAREN;
		int _saveIndex;
		
		match(')');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLBRACK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LBRACK;
		int _saveIndex;
		
		match('[');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRBRACK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RBRACK;
		int _saveIndex;
		
		match(']');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LCURLY;
		int _saveIndex;
		
		match('{');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RCURLY;
		int _saveIndex;
		
		match('}');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSEMI(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SEMI;
		int _saveIndex;
		
		match(';');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCOMMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMMA;
		int _saveIndex;
		
		match(',');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPATH(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PATH;
		int _saveIndex;
		
		match('!');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mRPATH(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = RPATH;
		int _saveIndex;
		
		match('^');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDATATYPE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DATATYPE;
		int _saveIndex;
		
		match("^^");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNAME_IT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NAME_IT;
		int _saveIndex;
		
		match(":-");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mARROW_R(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ARROW_R;
		int _saveIndex;
		
		match("=>");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mARROW_PATH_L(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ARROW_PATH_L;
		int _saveIndex;
		
		match(">-");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mARROW_PATH_R(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ARROW_PATH_R;
		int _saveIndex;
		
		match("->");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EQUAL;
		int _saveIndex;
		
		match("=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSL_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SL_COMMENT;
		int _saveIndex;
		
		match("#");
		{
		_loop163:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				{
				match(_tokenSet_0);
				}
			}
			else {
				break _loop163;
			}
			
		} while (true);
		}
		{
		if ((LA(1)=='\n'||LA(1)=='\r')) {
			mNL(false);
		}
		else {
		}
		
		}
		if ( inputState.guessing==0 ) {
			_ttype = Token.SKIP;
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NL;
		int _saveIndex;
		
		boolean synPredMatched170 = false;
		if (((LA(1)=='\r') && (LA(2)=='\n') && (true))) {
			int _m170 = mark();
			synPredMatched170 = true;
			inputState.guessing++;
			try {
				{
				mNL1(false);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched170 = false;
			}
			rewind(_m170);
			inputState.guessing--;
		}
		if ( synPredMatched170 ) {
			mNL1(false);
		}
		else {
			boolean synPredMatched172 = false;
			if (((LA(1)=='\n'))) {
				int _m172 = mark();
				synPredMatched172 = true;
				inputState.guessing++;
				try {
					{
					mNL2(false);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched172 = false;
				}
				rewind(_m172);
				inputState.guessing--;
			}
			if ( synPredMatched172 ) {
				mNL2(false);
			}
			else {
				boolean synPredMatched174 = false;
				if (((LA(1)=='\r') && (true) && (true))) {
					int _m174 = mark();
					synPredMatched174 = true;
					inputState.guessing++;
					try {
						{
						mNL3(false);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched174 = false;
					}
					rewind(_m174);
					inputState.guessing--;
				}
				if ( synPredMatched174 ) {
					mNL3(false);
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				}}
				if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
					_token = makeToken(_ttype);
					_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
				}
				_returnToken = _token;
			}
			
	protected final void mNL1(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NL1;
		int _saveIndex;
		
		match("\r\n");
		if ( inputState.guessing==0 ) {
			newline();
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNL2(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NL2;
		int _saveIndex;
		
		match("\n");
		if ( inputState.guessing==0 ) {
			newline();
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNL3(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NL3;
		int _saveIndex;
		
		match("\r");
		if ( inputState.guessing==0 ) {
			newline();
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WS;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case ' ':
		{
			match(' ');
			break;
		}
		case '\t':
		{
			match('\t');
			break;
		}
		case '\u000c':
		{
			match('\f');
			break;
		}
		case '\n':  case '\r':
		{
			mNL(false);
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			_ttype = Token.SKIP;
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NWS;
		int _saveIndex;
		
		{
		match(_tokenSet_9);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNUMERIC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NUMERIC;
		int _saveIndex;
		
		{
		matchRange('0','9');
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mQUOTE3S(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = QUOTE3S;
		int _saveIndex;
		
		match("'''");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mESCAPE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ESCAPE;
		int _saveIndex;
		char  ch = '\0';
		
		_saveIndex=text.length();
		match('\\');
		text.setLength(_saveIndex);
		{
		boolean synPredMatched213 = false;
		if (((_tokenSet_10.member(LA(1))) && ((LA(2) >= '\u0000' && LA(2) <= '\ufffe')) && (true))) {
			int _m213 = mark();
			synPredMatched213 = true;
			inputState.guessing++;
			try {
				{
				mESC_CHAR(false);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched213 = false;
			}
			rewind(_m213);
			inputState.guessing--;
		}
		if ( synPredMatched213 ) {
			mESC_CHAR(false);
		}
		else if (((LA(1) >= '\u0000' && LA(1) <= '\ufffe')) && ((LA(2) >= '\u0000' && LA(2) <= '\ufffe')) && (true)) {
			ch = LA(1);
			matchNot(EOF_CHAR);
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\\"+ch) ;
			}
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mQUOTE3D(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = QUOTE3D;
		int _saveIndex;
		
		match('"');
		match('"');
		match('"');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mESC_CHAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ESC_CHAR;
		int _saveIndex;
		Token h=null;
		
		{
		switch ( LA(1)) {
		case 'n':
		{
			match('n');
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\n") ;
			}
			break;
		}
		case 'r':
		{
			match('r');
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\r") ;
			}
			break;
		}
		case 'b':
		{
			match('b');
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\b") ;
			}
			break;
		}
		case 't':
		{
			match('t');
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\t") ;
			}
			break;
		}
		case 'f':
		{
			match('f');
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\f") ;
			}
			break;
		}
		case 'v':
		{
			match('v');
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\f") ;
			}
			break;
		}
		case 'a':
		{
			match('a');
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\007") ;
			}
			break;
		}
		case 'u':
		{
			match('u');
			mHEX4(true);
			h=_returnToken;
			if ( inputState.guessing==0 ) {
				
								char ch = (char)Integer.parseInt(h.getText(), 16) ;
								text.setLength(_begin); text.append(ch) ;
								
			}
			break;
		}
		case '"':
		{
			match('"');
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\"") ;
			}
			break;
		}
		case '\\':
		{
			match('\\');
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("\\") ;
			}
			break;
		}
		case '\'':
		{
			match('\'');
			if ( inputState.guessing==0 ) {
				text.setLength(_begin); text.append("'") ;
			}
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mHEX4(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = HEX4;
		int _saveIndex;
		
		mHEX_DIGIT(false);
		mHEX_DIGIT(false);
		mHEX_DIGIT(false);
		mHEX_DIGIT(false);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mHEX_DIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = HEX_DIGIT;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			matchRange('0','9');
			break;
		}
		case 'A':  case 'B':  case 'C':  case 'D':
		case 'E':  case 'F':
		{
			matchRange('A','F');
			break;
		}
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':
		{
			matchRange('a','f');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[2048];
		data[0]=-9217L;
		for (int i = 1; i<=1022; i++) { data[i]=-1L; }
		data[1023]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = new long[1025];
		data[1]=576460743847706622L;
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = new long[3988];
		data[0]=576223257791823872L;
		data[1]=576460745995190270L;
		data[2]=36028797018963968L;
		for (int i = 3; i<=12; i++) { data[i]=-1L; }
		data[13]=-4611686018427387905L;
		for (int i = 14; i<=127; i++) { data[i]=-1L; }
		data[128]=-9223372036854763520L;
		data[129]=-281474976710655L;
		for (int i = 130; i<=133; i++) { data[i]=-1L; }
		data[134]=65535L;
		for (int i = 176; i<=190; i++) { data[i]=-1L; }
		data[191]=281474976710655L;
		data[192]=-2L;
		for (int i = 193; i<=863; i++) { data[i]=-1L; }
		for (int i = 996; i<=1022; i++) { data[i]=-1L; }
		data[1023]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = new long[3988];
		data[0]=576214461698801664L;
		data[1]=576460745995190270L;
		data[2]=36028797018963968L;
		for (int i = 3; i<=12; i++) { data[i]=-1L; }
		data[13]=-4611686018427387905L;
		for (int i = 14; i<=127; i++) { data[i]=-1L; }
		data[128]=-9223372036854763520L;
		data[129]=-281474976710655L;
		for (int i = 130; i<=133; i++) { data[i]=-1L; }
		data[134]=65535L;
		for (int i = 176; i<=190; i++) { data[i]=-1L; }
		data[191]=281474976710655L;
		data[192]=-2L;
		for (int i = 193; i<=863; i++) { data[i]=-1L; }
		for (int i = 996; i<=1022; i++) { data[i]=-1L; }
		data[1023]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = new long[1025];
		data[0]=287992881640112128L;
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = new long[2048];
		data[0]=-576179277326712833L;
		data[1]=-576460743847706623L;
		for (int i = 2; i<=1022; i++) { data[i]=-1L; }
		data[1023]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = new long[1025];
		data[0]=287948901175001088L;
		data[1]=576460743847706622L;
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = new long[3988];
		data[0]=287984085547089920L;
		data[1]=576460745995190270L;
		data[2]=36028797018963968L;
		for (int i = 3; i<=12; i++) { data[i]=-1L; }
		data[13]=-4611686018427387905L;
		for (int i = 14; i<=127; i++) { data[i]=-1L; }
		data[128]=-9223372036854763520L;
		data[129]=-281474976710655L;
		for (int i = 130; i<=133; i++) { data[i]=-1L; }
		data[134]=65535L;
		for (int i = 176; i<=190; i++) { data[i]=-1L; }
		data[191]=281474976710655L;
		data[192]=-2L;
		for (int i = 193; i<=863; i++) { data[i]=-1L; }
		for (int i = 996; i<=1022; i++) { data[i]=-1L; }
		data[1023]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = new long[2048];
		data[0]=-1L;
		data[1]=-268435457L;
		for (int i = 2; i<=1022; i++) { data[i]=-1L; }
		data[1023]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = new long[2048];
		data[0]=-4294981121L;
		for (int i = 1; i<=1022; i++) { data[i]=-1L; }
		data[1023]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = new long[1025];
		data[0]=566935683072L;
		data[1]=32721766958759936L;
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	
	}