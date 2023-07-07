package com.example.mygame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int playerCurrentRow,playerCurrentColumn;
    int pointCounter;
    String[] mazeText;
    TextView punkteStand;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        EditText textView = findViewById(R.id.mazeTextField);
        punkteStand = findViewById(R.id.punkteStand);

        Maze maze = new Maze(10, 18);
        maze.connectPerfectly(new Random());
        mazeText = maze.asTileText();
        setTreasure(mazeText);
        setPlayer(mazeText);
        StringBuilder sb = new StringBuilder();
        for (String line : mazeText) {
            sb.append(line);
            sb.append("\n");
        }
        textView.setText(sb);


        Button north = (Button) findViewById(R.id.North);
        Button west = (Button) findViewById(R.id.West);
        Button east = (Button) findViewById(R.id.East);
        Button south = (Button) findViewById(R.id.South);
        north.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                movePlayer(Maze.Direction.NORTH);
                updateMazeTextView(textView);
            }

            });
        west.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                movePlayer(Maze.Direction.WEST);
                updateMazeTextView(textView);
            }

        });
        east.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                movePlayer(Maze.Direction.EAST);
                updateMazeTextView(textView);
            }

        });
        south.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                movePlayer(Maze.Direction.SOUTH);
                updateMazeTextView(textView);
            }

        });
        }

    void setPlayer(String [] field){
        Random random = new Random();

        while(true){
            int randomIndex1 = random.nextInt(field.length);
            int randomIndex2 = random.nextInt(field[randomIndex1].length());


            char[] charArray = field[randomIndex1].toCharArray();
            if (charArray[randomIndex2] == ' '&& charArray[randomIndex2]!='X') {
                charArray[randomIndex2] = 'X';
                field[randomIndex1] = new String(charArray);
                playerCurrentRow=randomIndex1;
                playerCurrentColumn = randomIndex2;
                break;
            }
        }




    }
    void setTreasure(String [] field){
        Random random = new Random();
        int amountTreasures = 0;
        while(amountTreasures<=4){
            int randomIndex1 = random.nextInt(field.length);
            int randomIndex2 = random.nextInt(field[randomIndex1].length());


            char[] charArray = field[randomIndex1].toCharArray();
            if (charArray[randomIndex2] == ' ') {
                charArray[randomIndex2] = 'T';
                field[randomIndex1] = new String(charArray);
                playerCurrentRow=randomIndex1;
                playerCurrentColumn = randomIndex2;
                amountTreasures++;
            }
        }
    }
    void movePlayer(Maze.Direction direction){
        int newRow = playerCurrentRow + direction.deltaRow;
        int newCol = playerCurrentColumn + direction.deltaCol;
        if(isValidMove(newRow,newCol)){

            char[]newCharArray = mazeText[newRow].toCharArray();
            if(newCharArray[newCol]=='T'){
                pointCounter++;
                punkteStand.setText("Punktestand :"+pointCounter);
            }
            newCharArray[newCol] = 'X';
            mazeText[newRow] = new String(newCharArray);

            char[]currentCharArray = mazeText[playerCurrentRow].toCharArray();
            currentCharArray[playerCurrentColumn] = ' ';
            mazeText[playerCurrentRow] = new String(currentCharArray);

            playerCurrentRow = newRow;
            playerCurrentColumn = newCol;

        }

    }
    boolean isValidMove(int row, int column) {
        if (row >= 0 && row < mazeText.length && column >= 0 && column < mazeText[row].length()) {
            return mazeText[row].charAt(column) == ' '||mazeText[row].charAt(column) == 'T';
        }
        return false;
    }
    void updateMazeTextView(EditText textView) {
        StringBuilder sb = new StringBuilder();
        for (String line : mazeText) {
            sb.append(line);
            sb.append("\n");
        }
        textView.setText(sb.toString());
    }



}