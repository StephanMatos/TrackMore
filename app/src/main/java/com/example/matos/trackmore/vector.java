package com.example.matos.trackmore;


public class vector {

        double x, y;

        public vector(){
            this.x = 0;
            this.y = 0;
        }

        public vector(double x, double y){
            this.x = x;
            this.y = y;
        }

        void add(vector v){
            this.x += v.x;
            this.y += v.y;
        }

        void sub(vector v){
        this.x -= v.x;
        this.y -= v.y;
        }

        double scalar(vector v){
            return this.x*v.x + this.y*v.y;
        }

        double length(){
            double length = 0;
            length = Math.sqrt(x * x + y * y);
            return length;
        }

        double angle(vector v){

            double angle = Math.acos(scalar(v)/(v.length()*length()));

            System.out.println(" This is scalar product "+ scalar(v));

            return angle;
        }


        void print(){
            System.out.println("Vector is:");
            System.out.println("x: " + x);
            System.out.println("y: " + y);
            System.out.println("Length: " + length());
            System.out.println();
        }

    }


