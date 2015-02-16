package lab3;

import java.io.*;

public class Template {

    private static PrintWriter out;

    public static void main(String args[]) {


        //sample codes
        shape1(9);
        System.out.println();
        shape2(9);
        System.out.println();


        //open a file in write mode "c:\\lab3.txt"

        String path = "C:\\lab3.txt";
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(path)));
        }
        catch (IOException e) {
            System.out.println ("IOException!!!");
        }

        //append following  patterns in a file e.g. "c:\\lab3.txt" one by one

        shape3(9);
        shape4(9);
        shape5(9);
        shape6(9);


        //read the pattern File e.g. "c://lab3.txt" and display to the screen

        try{
            out.close();
            BufferedReader input = new BufferedReader (new FileReader (path));
            String line;
            while ((line=input.readLine())!=null) {
                System.out.println(line);
            }
            input.close ();
        }
        catch (IOException e) {
            System.out.println ("IOException!!!");
        }

    }

    //sample pattern printing to the screen
    public static void shape1(int j) {
        for (int i = 1; i <= j; i++) {
            for (int k = 1; k <= i; k++) {
                System.out.print(i);
            }
            System.out.println();
        }

    }


    //sample pattern printing to the screen
    public static void shape2(int j) {
        for (int i = 1; i <= j; i++) {
            for (int k = 1; k <= (j * 2 + 1); k++) {
                System.out.print(i);
            }
            System.out.println();
        }

    }
	
	/*
	 * 
1111111111111111111
2........2........2
3........3........3
4........4........4
5........5........5
6........6........6
7........7........7
8........8........8
9999999999999999999
	
	* 
	*/


    //save the pattern in a file
    //need to handle the exceptions
    public static void shape3(int j) {

    for (int i = 1; i <= j; i ++) {
        char spclChr;
        if (i == 1 || i == 9) {
            spclChr = Character.forDigit(i, 10);
        }
        else {
            spclChr = '.';
        }

        for (int k = 0; k < 2; k ++) {
            out.print(i);
            for (int l = 0; l < 8; l ++) {
                out.print(spclChr);
            }
        }
        out.print(i);
        out.println();
    }



    }
	
	/*
	 * 
1111111111111111111
2........2........2
3........3........3
4........4........4
5555555555555555555
6........6........6
7........7........7
8........8........8
9999999999999999999
	
	* 
	*/

    //save the pattern in a file
    //need to handle the exceptions
    public static void shape4(int j) {

        for (int i = 1; i <= j; i ++) {
            char spclChr;
            if (i == 1 || i == 5 || i == 9) {
                spclChr = Character.forDigit(i, 10);
            }
            else {
                spclChr = '.';
            }

            for (int k = 0; k < 2; k ++) {
                out.print(i);
                for (int l = 0; l < 8; l ++) {
                    out.print(spclChr);
                }
            }
            out.print(i);
            out.println();
        }
    }
	
	/*
	 * 
1111111111111111111
1..x..x..1..x..x..1
1..x..x..1..x..x..1
1..x..x..1..x..x..1
1111111111111111111
1..x..x..1..x..x..1
1..x..x..1..x..x..1
1..x..x..1..x..x..1
1111111111111111111
	
	* 
	*/

    //save the pattern in a file
    //need to handle the exceptions
    public static void shape5(int j) {

        for (int i = 1; i <= j; i ++) {
            boolean flag;
            if (i == 1 || i == 5 || i == 9) {
                flag = true;
            }
            else {
                flag = false;
            }

            for (int k = 0; k < 2; k ++) {
                out.print(1);
                for (int l = 0; l < 8; l ++) {
                    if (flag) {
                        out.print(1);
                    }
                    else {
                        if ((l + 1) % 3 == 0) {
                            out.print('x');
                        } else {
                            out.print('.');
                        }
                    }
                }
            }
            out.print(1);
            out.println();
        }
    }

	/*
	 * 
.........1.........
........222........
.......33333.......
......4444444......
.....555555555.....
....66666666666....
...7777777777777...
..888888888888888..
.99999999999999999.
	
	* 
	*/

    //save the pattern in a file
    //need to handle the exceptions
    public static void shape6(int j) {
				
        for (int i = 0; i < j; i ++) {
            for (int k = 0; k < 9 - i; k ++) {
                out.print('.');
            }
            for (int k = 0; k < 2*i + 1; k ++) {
                out.print(i + 1);
            }
            for (int k = 0; k < 9 - i; k ++) {
                out.print('.');
            }
            out.println();
        }
    }



}