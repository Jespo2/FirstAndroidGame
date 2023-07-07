package com.example.mygame;

import java.util.Random;

class Maze {

   static final char WALL = '#';
   static final char FREE = ' ';

   int rowCount, colCount;
   Cell[][] cells;

   Maze (int m, int n) {
      rowCount = m;
      colCount = n;
      cells = new Cell[rowCount][colCount];
      for (int row = 0; row < rowCount; row++)
         for (int col = 0; col < colCount; col++)
            cells[row][col] = new Cell(row, col);
   }

   enum Direction {
      // the four compass directions with some
      // capabilities to compute coordinates for
      // going in that direction
      NORTH(-1, 0),
      EAST(0, +1),
      SOUTH(+1, 0),
      WEST(0, -1);

      int deltaRow, deltaCol;
      Direction opposite;
      
      Direction (int dr, int dc) {
         deltaRow = dr; deltaCol = dc;
      }

      static void linkOpposites (Direction dir1, Direction dir2) {
         dir1.opposite = dir2;
         dir2.opposite = dir1;
      }

      static {
         linkOpposites(NORTH, SOUTH);
         linkOpposites(WEST, EAST);
      }
   }

   static final int DIR_COUNT = Direction.values().length;


   class Cell {
      int rowNr, colNr;
      int connectionCount;
      Direction[] connections;

      Cell (int row, int col) {
         rowNr = row;
         colNr = col;
         connectionCount = 0;
         connections = new Direction[DIR_COUNT];
      }

      Cell getNeighbor (Direction dir) {
         int neighborRow = this.rowNr + dir.deltaRow;
         int neighborCol = this.colNr + dir.deltaCol;
         if (0 <= neighborRow && neighborRow < rowCount &&
             0 <= neighborCol && neighborCol < colCount)
            return cells[neighborRow][neighborCol];
         else
            return null;
      }

      void connect (Direction dir) {
         if (isConnected(dir)) return;  // already connected
         Cell neighbor = getNeighbor(dir);
         assert neighbor != null : "cannot connect " + dir + " to non-existing neighbor";
         connections[connectionCount++] = dir;
         // add reverse connection from neighbor to this cell, too
         neighbor.connections[neighbor.connectionCount++] = dir.opposite;
      }

      boolean isConnected (Direction dir) {
         for (int i = 0; i < connectionCount; i++)
            if (connections[i] == dir) return true;
         return false;
      }
   }

   Direction pickSomeNewNeighbor (Cell c, int maxConnections, Random rand) {
      Direction[] candidates = new Direction[DIR_COUNT];
      int candidateCount = 0;
      for (Direction dir: Direction.values()) {
         Cell neighbor = c.getNeighbor(dir);
         if (neighbor != null && !c.isConnected(dir) &&
             neighbor.connectionCount <= maxConnections)
            candidates[candidateCount++] = dir;
      }
      if (candidateCount > 0)
         return candidates[rand.nextInt(candidateCount)];
      else
         return null;
   }

   Direction pickIsolatedNeighbor (Cell c, Random rand) {
      return pickSomeNewNeighbor(c, 0, rand);
   }
      
   void connectPerfectlyAt (Cell c, Random rand) {
      for (;;) {
         Direction pick = pickIsolatedNeighbor(c, rand);
         if (pick == null) break;
         c.connect(pick);
         connectPerfectlyAt(c.getNeighbor(pick), rand);
      }
   }

   void connectPerfectly (Random rand) {
      int anyRow = rand.nextInt(rowCount);
      int anyCol = rand.nextInt(colCount);
      connectPerfectlyAt(cells[anyRow][anyCol], rand);
   }

   Direction pickNewDeadEndNeighbor (Cell c, Random rand) {
      return pickSomeNewNeighbor(c, 1, rand);
   }

   Direction pickAnyNewNeighbor (Cell c, Random rand) {
      return pickSomeNewNeighbor(c, DIR_COUNT, rand);
   }

   void braidSomeDeadEnds (Random rand) {
      for (int row = 0; row < rowCount; row++)
         for (int col = 0; col < colCount; col++) {
            Cell c = cells[row][col];
            if (c.connectionCount == 1 && rand.nextBoolean()) {
               Direction pick = pickNewDeadEndNeighbor(c, rand);
               if (pick == null)
                  pick = pickAnyNewNeighbor(c, rand);
               c.connect(pick);
            }
         }
   }

   static char passage (Cell c, Direction dir) {
      if (c.isConnected(dir)) return FREE;
      else return WALL;
   }

   String buildWallLine (int row, Direction dir) {
      StringBuilder sb = new StringBuilder(2 * colCount + 1);
      sb.append(WALL);
      for (int col = 0; col < colCount; col++) {
         sb.append(passage(cells[row][col], dir));
         sb.append(WALL);
      }     
      return sb.toString();
   }

   String buildCellLine (int row) {
      StringBuilder sb = new StringBuilder(2 * colCount + 1);
      sb.append(passage(cells[row][0], Direction.WEST));
      for (int col = 0; col < colCount; col++) {
         sb.append(FREE);
         sb.append(passage(cells[row][col], Direction.EAST));
      }     
      return sb.toString();      
   }

   String[] asTileText () {
      String[] lines = new String[2 * rowCount + 1];
      int ln = 0;
      lines[ln++] = buildWallLine(0, Direction.NORTH);
      for (int row = 0; row < rowCount; row++) {
         lines[ln++] = buildCellLine(row);
         lines[ln++] = buildWallLine(row, Direction.SOUTH);
      }
      return lines;
   }

}
