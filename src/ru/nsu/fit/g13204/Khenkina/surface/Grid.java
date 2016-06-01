package ru.nsu.fit.g13204.Khenkina.surface;

public class Grid {
    public static final int MAX_M = 1000;
    public static final int MAX_N = 1000;
    public static final int MAX_K = 1000;


    public int m;
    public int n;
    public int k;

    public Grid(){

        m = 10;
        n = 10;
        k = 7;
    }

    public Grid(int m, int n, int k){

        this.m = m;
        this.n = n;
        this.k = k;
    }

    public Grid(Grid g){

        m = g.m;
        n = g.n;
        k = g.k;
    }

    @Override
    public boolean equals(Object o){
        Grid g2 = (Grid)o;
        return (m == g2.m && n == g2.n && k == g2.k);
    }

}
