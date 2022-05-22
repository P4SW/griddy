import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.company.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class VisitorTest {
    String outHeader = """
            /* === Code generated by Griddy compiler === */
            #include <stdio.h>
            #include <stdlib.h>
            #include <string.h>
            
            int main(int argc, char *argv[]){
            struct Piece { char* name; unsigned int limit; unsigned int count; };
            
            """,
            setupHeader = "/*    SETUP    */\n",
            gameHeader = "/*   GAME    */\n";

    String gameLoop(String body) {
        return """
                struct Player _current_player;
                int _turn_count = 0;
                do {
                """
                + body
                + """
                } while (0);
                
                return 0;
                }
                """;
    }

    @Test
    void outputString() {
        var input = """
                board (3,3);
                GAME
                    output "Hello, World!";
                """;
        var expected = outHeader
                + setupHeader
                + """
                struct Player {
                } _p1, _p2;
                struct Piece *_board[3][3] = {{NULL,NULL,NULL,},{NULL,NULL,NULL,},{NULL,NULL,NULL,},};
                                
                """
                + gameHeader
                + gameLoop("printf(\"%s\\n\", \"Hello, World!\");\n");

        var sb = new StringBuilder();
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        Griddy.main(Target.C, false, inputStream, sb);

        assertEquals(expected, sb.toString());
    }

    @Test
    void outputNumber() {
        var input = """
                board (3,3);
                GAME
                    output 42;
                """;
        var expected = outHeader
                + setupHeader
                + """
                struct Player {
                } _p1, _p2;
                struct Piece *_board[3][3] = {{NULL,NULL,NULL,},{NULL,NULL,NULL,},{NULL,NULL,NULL,},};

                """
                + gameHeader
                + gameLoop("printf(\"%d\\n\", 42);\n");

        var sb = new StringBuilder();
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        Griddy.main(Target.C, false, inputStream, sb);

        assertEquals(expected, sb.toString());
    }

    @Test
    void outputBoolean() {
        var input = """
                board (3,3);
                GAME
                    output true;
                """;
        var expected = outHeader
                + setupHeader
                + """
                struct Player {
                } _p1, _p2;
                struct Piece *_board[3][3] = {{NULL,NULL,NULL,},{NULL,NULL,NULL,},{NULL,NULL,NULL,},};
                
                """
                + gameHeader
                + gameLoop("printf(\"%d\\n\", 1);\n");

        var sb = new StringBuilder();
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        Griddy.main(Target.C, false, inputStream, sb);

        assertEquals(expected, sb.toString());
    }

    @Test
    void pieceDef() {
        var input = """
                board (3,3);
                piece x_piece
                    name: "X"
                    limit: 3
                end
                piece y_piece
                    name: "Y"
                    limit: 3
                end
                GAME
                """;

        var expected = outHeader
                + setupHeader
                + """
                struct Player {
                  struct Piece x_piece;
                  struct Piece y_piece;
                } _p1, _p2;
                _p1.x_piece.name = calloc(2, sizeof(char));
                strcpy(_p1.x_piece.name, "X");
                _p1.x_piece.limit = 3;
                _p1.x_piece.count = 0;
                _p1.y_piece.name = calloc(2, sizeof(char));
                strcpy(_p1.y_piece.name, "Y");
                _p1.y_piece.limit = 3;
                _p1.y_piece.count = 0;
                _p2.x_piece.name = calloc(2, sizeof(char));
                strcpy(_p2.x_piece.name, "X");
                _p2.x_piece.limit = 3;
                _p2.x_piece.count = 0;
                _p2.y_piece.name = calloc(2, sizeof(char));
                strcpy(_p2.y_piece.name, "Y");
                _p2.y_piece.limit = 3;
                _p2.y_piece.count = 0;
                struct Piece *_board[3][3] = {{NULL,NULL,NULL,},{NULL,NULL,NULL,},{NULL,NULL,NULL,},};
                
                """
                + gameHeader
                + gameLoop("");

        var sb = new StringBuilder();
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        Griddy.main(Target.C, false, inputStream, sb);

        assertEquals(expected, sb.toString());
    }
}