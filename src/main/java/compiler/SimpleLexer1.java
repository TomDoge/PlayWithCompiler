package compiler;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class SimpleLexer1 {

    public static void main(String args[]) {
        SimpleLexer1 lexer = new SimpleLexer1();
        lexer.tokenize("3+4*5");
    }

    private StringBuffer tokenText = null;
    private List<Token> tokens = null;
    private SimpleToken token = null;

    private boolean isAlpha(int ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
    }

    private boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    private DfaState initToken(char ch) {
        if (tokenText.length() > 0) {          
            token.text=tokenText.toString();
            tokens.add(token);
            System.out.println(token.getText());

            tokenText = new StringBuffer();
            token = new SimpleToken();
        }

        DfaState newState = DfaState.Initial;
        if (isAlpha(ch)) {
            newState = DfaState.Id;
            token.type = TokenType.Identifier;
            tokenText.append(ch);
        } else if (isDigit(ch)) {
            newState = DfaState.IntConstant;
            token.type = TokenType.IntConstant;
            tokenText.append(ch);
        } else if (ch == '>') {
            newState = DfaState.GT;
            token.type = TokenType.GT;
            tokenText.append(ch);
        } else if (ch == '+') {
            newState = DfaState.Plus;
            token.type = TokenType.Plus;
            tokenText.append(ch);
        } else if (ch == '-') {
            newState = DfaState.Minus;
            token.type = TokenType.Minus;
            tokenText.append(ch);
        } else if (ch == '*') {
            newState = DfaState.Star;
            token.type = TokenType.Star;
            tokenText.append(ch);
        } else {
            newState = DfaState.Initial; // skip all unknown patterns
        }
        return newState;
    }

    public SimpleTokenReader tokenize(String code) {
        tokens = new ArrayList<Token>();
        CharArrayReader reader = new CharArrayReader(code.toCharArray());
        tokenText = new StringBuffer();
        token = new SimpleToken();
        int ich = 0;
        char ch = 0;
        DfaState state = DfaState.Initial;
        try {
            while ((ich = reader.read()) != -1) {
                ch = (char) ich;
                switch (state) {
                case Initial:
                    state = initToken(ch);
                    break;
                case Id:
                    if (isAlpha(ch) || isDigit(ch)) {
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case GT:
                    if (ch == '=') {
                        token.type = TokenType.GE;
                        state = DfaState.GE;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case GE:
                case Plus:
                case Minus:
                case Star:
                    state = initToken(ch);                 
                    break;
                case IntConstant:
                    if (isDigit(ch)) {
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                default:
                    
                }
            

            }
            // 把最后一个token送进去
            if (tokenText.length() > 0) {
                initToken(ch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new SimpleTokenReader(tokens);
    }

    private final class SimpleToken implements Token{
        private TokenType type=null;
        private String text = null;

        // public SimpleToken(){ }

        // public SimpleToken(TokenType type,String text){
        //     this.type = type;
        //     this.text = text;
        // }

        @Override
        public TokenType getType() {
            return type;
        }

        @Override
        public String getText() {
            return text;
        }

        // public void setType(TokenType type){
        //     this.type = type;
        // }

        // public void setText(String text){
        //     this.text = text;
        // }


    }

    private enum DfaState{
        Initial,

        If,
        Id_if1,
        Id_if2,
        Else,
        Id_else1,
        Id_else2,
        Id_else3,
        Id_else4,
        Int,
        Id_int1,
        Id_int2,
        Id_int3,
        Id,
        GT,
        GE,

        Plus,
        Minus,
        Star,

        IntConstant
    }

    private class SimpleTokenReader implements TokenReader{
        List<Token> tokens = null;
        int pos = 0;

        public SimpleTokenReader(List<Token> tokens){
            this.tokens = tokens;
        }

        @Override
        public Token read() {
            if (pos < tokens.size()){
                return tokens.get(pos++);
            }
            return null;
        }

        @Override
        /**
         * 返回Token流中下一个Token，但不从流中取出。
         * 如果流已经为空，返回null;
         */
        public Token peek() {
            if (pos < tokens.size()){
                return tokens.get(pos);
            }
            return null;
        }

        // @Override
        // public void unread() {
        //     if (pos > 0){
        //         pos --;
        //     }
        // }

    }

    

}