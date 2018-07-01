import java.io.*;
import java.util.*;

public class BattleShip1 {

    static class CoordsShip {
        int[][] coords;
        ArrayList<Ship> ships;

        public CoordsShip(int[][] coords1, ArrayList<Ship> ships1){
            coords = coords1;
            ships = ships1;
        }
    }

    static class Ship {
        int startX;
        int startY;
        int endX;
        int endY;
        int size;

        public Ship(int startx, int starty, int endx, int endy, int sSize) {
            startX = startx;
            startY = starty;
            endX = endx;
            endY = endy;
            size = sSize;
        }
    }

    public static void main(String args[]){
        try{
            BufferedReader r = new BufferedReader(new FileReader(args[0]));
            int solutions = Integer.parseInt(args[1]);
            int score;
            int[][] bestSol = null;
            int[] shipsArray = convertToIntArray(r.readLine().split(" "));
            int[] xHArray = convertToIntArray(r.readLine().split(" "));
            int[] yHArray = convertToIntArray(r.readLine().split(" "));
            int[][] coords = new int[xHArray.length][yHArray.length];
            ArrayList<Ship> ships = new ArrayList<Ship>();
            ArrayList<Ship> bestShips = new ArrayList<Ship>();

            for(int i = 0; i < shipsArray.length; i++){
                int ship = shipsArray[i];
                CoordsShip temp = placeShip(ship, xHArray, yHArray, coords, ships);
                if(temp == null){
                    i = -1;
                    coords = new int[xHArray.length][yHArray.length];
                    ships = new ArrayList<Ship>();
                }
                else{
                    coords = copyArray(temp.coords);
                    ships = copyArrayList(temp.ships);
                    coords = copyArray(setWater(yHArray, xHArray, coords));
                }

            }
            /*map2DArray(yHArray, xHArray, coords);*/
            score = getScore(xHArray, yHArray, coords);
            bestSol = copyArray(coords);
            bestShips = copyArrayList(ships);

            while(score!=0&&(solutions--) !=0) {
                CoordsShip cs = hillClimb(ships, coords, xHArray, yHArray);
                coords = copyArray(cs.coords);
                ships = copyArrayList(cs.ships);
                /*System.out.println();
                map2DArray(yHArray, xHArray, coords);
                System.out.println();*/

                if(score >= getScore(xHArray, yHArray, coords)){
                    score = getScore(xHArray, yHArray, coords);
                    //check if solution valid
                    bestSol = copyArray(coords);
                    bestShips = copyArrayList(ships);
                }
                else{
                    coords = copyArray(bestSol);
                    ships = copyArrayList(bestShips);
                }
            }

            /*for(int i = 0; i < ships.size(); i++){
                System.out.println(ships.get(i).startX + " " + ships.get(i).endX + " " + ships.get(i).startY + " " + ships.get(i).endY);
            }*/
            map2DArray(yHArray, xHArray, bestSol);
            System.out.println(score);


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static CoordsShip placeShip(int shipSize, int[] xHArray, int[] yHArray, int[][] coords1, ArrayList<Ship> ships1) {
        int [][] coords = copyArray(coords1);
        ArrayList<Ship> ships = copyArrayList(ships1);
        Random r = new Random();
        int[][] xC;
        int[][] yC;
        int cX;
        int cY;
        int x;
        int y;
        int counter = 0;
        int thresh = 0;
        int maxT = xHArray.length * yHArray.length;

        boolean b;

        while (true) {
            thresh++;
            x = r.nextInt(xHArray.length);
            y = r.nextInt(yHArray.length);

            xC = new int[xHArray.length][yHArray.length];
            yC = new int[xHArray.length][yHArray.length];

            cX = 0;
            cY = 0;
            boolean right = false, left = false, up = false, down = false;

            if (coords[x][y] == 0) {
                cX++;
                cY++;
                xC[x][y] = 1;
                yC[x][y] = 1;
                for (int i = 1; i < shipSize; i++) { // for the shipSize
                    if (x + i < xHArray.length && coords[x + i][y] == 0 && right == false) {
                        xC[x + i][y] = 1;
                        cX++;
                    } else {
                        right = true;
                    }
                    if (x - i >= 0 && coords[x - i][y] == 0 && left == false) {
                        xC[x - i][y] = 1;
                        cX++;
                    } else {
                        left = true;
                    }
                    if (y + i < yHArray.length && coords[x][y + i] == 0 && up == false) {
                        yC[x][y + i] = 1;
                        cY++;
                    } else {
                        up = true;
                    }
                    if (y - i >= 0 && coords[x][y - i] == 0 && down == false) {
                        yC[x][y - i] = 1;
                        cY++;
                    } else {
                        down = true;
                    }
                }
            }

            if (cX >= shipSize || cY >= shipSize) {
                break;
            }
            if (thresh == maxT) {
                return null;
            }
        }

        boolean makeShip = false;
        Ship s = new Ship(0, 0 ,0 ,0 ,0);

        if(cX >= shipSize && cX > cY){
            for(int i = 0; i < xHArray.length; i++){
                if(xC[i][y] == 1){
                    if(!makeShip){
                        makeShip = true;
                        s.startX = i;
                        s.startY = y;
                        s.endY = y;
                        s.size = shipSize;
                    }
                    coords[i][y] = 1;
                    counter++;
                    if(counter == shipSize){
                        //error is possibly here one positon in from end, on the right side
                        s.endX = counter + s.startX - 1;
                        ships.add(s);
                        break;
                    }
                }
            }
        }
        else{
            for(int i = 0; i < yHArray.length; i++){
                if(yC[x][i] == 1){
                    if(!makeShip){
                        makeShip = true;
                        s.startX = x;
                        s.startY = i;
                        s.endX = x;
                        s.size = shipSize;
                    }
                    coords[x][i] = 1;
                    counter++;
                    if(counter == shipSize){
                        //error is possibly here one position in from end on the right side
                        s.endY = counter + s.startY - 1;
                        ships.add(s);
                        break;
                    }
                }
            }

        }
        CoordsShip cs = new CoordsShip(coords, ships);
        return cs;
    }

    private static int[][] setWater(int[] yHArray, int[] xHArray, int[][] coords1) {
        int [][] coords = copyArray(coords1);
        for (int y = 0; y < yHArray.length; y++) {
            for (int x = 0; x < xHArray.length; x++) {
                if (coords[x][y] == 1) {
                    if (x - 1 >= 0 && coords[x - 1][y] != 1) {
                        coords[x - 1][y] = 2;
                        if (y - 1 >= 0 && coords[x - 1][y - 1] != 1) coords[x - 1][y - 1] = 2;
                        if (y + 1 < yHArray.length && coords[x - 1][y + 1] != 1) coords[x - 1][y + 1] = 2;
                    }
                    if (x + 1 < xHArray.length && coords[x + 1][y] != 1) {
                        coords[x + 1][y] = 2;

                        if (y - 1 >= 0 && coords[x + 1][y - 1] != 1) coords[x + 1][y - 1] = 2;
                        if (y + 1 < yHArray.length && coords[x + 1][y + 1] != 1) coords[x + 1][y + 1] = 2;
                    }
                    if (y - 1 >= 0 && coords[x][y - 1] != 1) coords[x][y - 1] = 2;
                    if (y + 1 < yHArray.length && coords[x][y + 1] != 1) coords[x][y + 1] = 2;
                }
            }
        }
        return coords;
    }

    private static int getScore(int[] xHArray, int[] yHArray, int[][] coords1) {
        int [][] coords = copyArray(coords1);
        int score = 0;
        int[] scoreX = new int[xHArray.length];
        int[] scoreY = new int[yHArray.length];
        for (int y = 0; y < yHArray.length; y++) {
            for (int x = 0; x < xHArray.length; x++) {
                if(coords[x][y] == 1){
                   /* scoreX[x] = scoreX[x] + 1;
                    scoreY[y] = scoreY[y] + 1;*/
                    scoreX[x]++;
                    scoreY[y]++;
                }

            }
        }

        for(int i = 0; i < xHArray.length; i++){
            score = score + Math.abs(xHArray[i] - scoreX[i]) + Math.abs(yHArray[i] - scoreY[i]);
        }

        return score;
    }

    private static CoordsShip hillClimb(ArrayList<Ship> ships1, int[][] coords1, int[] xHArray, int[] yHArray) {
        int [][] coords = copyArray(coords1);
        int[][] temp = copyArray(coords);
        ArrayList<Ship> ships = copyArrayList(ships1);
        Random r = new Random();
        int shipI = r.nextInt(ships.size());
        ArrayList<Ship> ships2 = copyArrayList(ships);
        Ship s = ships.get(shipI);
        ships.remove(shipI);

        coords = deleteShip(s, coords, xHArray, yHArray);

       /* System.out.println("--");
        map2DArray(yHArray, xHArray, coords);*/
        CoordsShip cs = placeShip(s.size, xHArray, yHArray, coords, ships);

        if(cs == null){
           /* System.out.println("didn't work");*/
            coords = copyArray(temp);
            //ships.add(s);
            ships = copyArrayList(ships2);
        }else{
            coords = copyArray(cs.coords);
            ships = copyArrayList(cs.ships);
            /*System.out.println("..added ship above");
            map2DArray(yHArray, xHArray, coords);*/
            //ships.remove(s);
            coords = copyArray(setWater(yHArray, xHArray, coords));
        }
        CoordsShip cs1 = new CoordsShip(coords, ships);
        return cs1;
    }

    private static void map2DArray(int[] yHArray, int[] xHArray, int[][] coords1) {
        int [][] coords = copyArray(coords1);
        for (int y = 0; y < yHArray.length; y++) {
            for (int x = 0; x < xHArray.length; x++) {
                if (coords[x][y] == 0) {
                    System.out.print("~ ");
                    //coords[x][y] = 2;
                }
                if (coords[x][y] == 1) {
                    System.out.print("X ");
                }
                if (coords[x][y] == 2) {
                    System.out.print("~ ");
                }
            }
            System.out.print("\n");

        }
    }

    private static int[][] deleteShip(Ship s1, int[][] coords1, int[] xHArray, int[] yHArray) {
        Ship s = s1;
        int [][] coords = copyArray(coords1);
        int xS = s.startX;
        int yS = s.startY;
        int xE = s.endX;
        int yE = s.endY;
      /*  System.out.println("Size: " + s.size + " XS: " + xS + " XE: " + xE + " YS: " + yS + " YE: " + yE);*/

        if(xS == xE){
            if(yS == yE){
                coords = deleteWater(xS, yS, coords, xHArray, yHArray);
            }
            else{
                for(int y = yS; y <= yE; y++){
                    coords = deleteWater(xS, y, coords, xHArray, yHArray);
                }
            }
        }
        else if(yS == yE){
            if(xS == xE){
                coords = deleteWater(xS, yS, coords, xHArray, yHArray);
            }
            else{
                for(int x = xS; x <= xE; x++){
                    coords = deleteWater(x, yS, coords, xHArray, yHArray);
                }
            }
        }
        coords = copyArray(setWater(yHArray, xHArray, coords));
        return coords;
    }

    private static int[][] deleteWater(int x, int y, int[][] coords1, int[] xHArray, int[] yHArray) {
        int [][] coords = copyArray(coords1);
        coords[x][y] = 0;

        if (x - 1 >= 0 && (coords[x - 1][y] == 2 || coords[x - 1][y] == 1)) {
            coords[x - 1][y] = 0;
            if (y - 1 >= 0 && (coords[x - 1][y - 1] == 2  || coords[x - 1][y - 1] == 1)) {
                coords[x - 1][y - 1] = 0;
            }
            if (y + 1 < yHArray.length && (coords[x - 1][y + 1] == 2  || coords[x - 1][y + 1] == 1)) {
                coords[x - 1][y + 1] = 0;
            }
        }
        if (x + 1 < xHArray.length && (coords[x + 1][y] == 2  || coords[x + 1][y] == 1)) {
            coords[x + 1][y] = 0;
            if (y - 1 >= 0 && (coords[x + 1][y - 1] == 2 || coords[x + 1][y - 1] == 1)) {
                coords[x + 1][y - 1] = 0;
            }
            if (y + 1 < yHArray.length && (coords[x + 1][y + 1] == 2 || coords[x + 1][y + 1] == 1)) {
                coords[x + 1][y + 1] = 0;
            }
        }
        if (y - 1 >= 0 && (coords[x][y - 1] == 2 || coords[x][y - 1] == 1)) {
            coords[x][y - 1] = 0;
        }
        if (y + 1 < yHArray.length && (coords[x][y + 1] == 2|| coords[x][y + 1] == 1)) {
            coords[x][y + 1] = 0;
        }

        return coords;
    }

    private static int[][] copyArray(int[][] a) {
        int[][] b = new int[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            System.arraycopy(a[i],0,b[i],0,a[0].length);
        }
        return b;
    }

    private static ArrayList<Ship> copyArrayList(ArrayList<Ship> a){
        ArrayList<Ship> b = new ArrayList<Ship>();
        for (int i = 0; i < a.size(); i++) {
            b.add(a.get(i));
        }
        return b;
    }

    private static int[] convertToIntArray(String[] s) {
        int[] ia = new int[s.length];
        for (int i = 0; i < ia.length; i++) {
            ia[i] = Integer.parseInt(s[i]);
        }
        return ia;
    }
}
