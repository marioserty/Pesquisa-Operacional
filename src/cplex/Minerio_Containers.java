/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cplex;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 *
 * @author mario
 */
public class Minerio_Containers {

    public static void main(String[] args) throws IloException, IOException {
        new Minerio_Containers();
    }

    public Minerio_Containers() throws IloException, FileNotFoundException, IOException, IOException {

        int n = 100;
        int limitePeso = 500;
        int limiteVolume = 70;
        int limiteItens = 5;
        int bags = 3;

        double[] item = new double[n];
        double[] valor = new double[n];
        double[] peso = new double[n];
        double[] volume = new double[n];

        String arquivoCSV = "instancia_p1.csv";
        BufferedReader br = null;
        String linha = "";
        String csvDivisor = ",";
        br = new BufferedReader(new FileReader(arquivoCSV));
        linha = br.readLine();
        //int j = 0;
        while ((linha = br.readLine()) != null) {

            String[] l = linha.split(csvDivisor);
            item[Integer.valueOf(l[0]) - 1] = Double.valueOf(l[0]);
            valor[Integer.valueOf(l[0]) - 1] = Double.valueOf(l[1]);
            peso[Integer.valueOf(l[0]) - 1] = Double.valueOf(l[2]);
            volume[Integer.valueOf(l[0]) - 1] = Double.valueOf(l[3]);
            //System.out.println(item[j] + " : " + valor[j] + " : "+ peso[j] + " : " + volume[j] + " : ");
            //j++;
        }

        IloCplex model = new IloCplex();

        //--------------Variáveis de Decisão--------------
        //Itens
        IloNumVar[][] x = new IloNumVar[n][bags];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < bags; j++) {
                x[i][j] = model.numVar(0, limiteItens);
            }
        }

        //--------------Função Objetivo--------------
        IloLinearNumExpr funcaoObjetivoExp = model.linearNumExpr();
        for (int j = 0; j < bags; j++) {
            for (int i = 0; i < n; i++) {
                funcaoObjetivoExp.addTerm(valor[i], x[i][j]);
            }
        }
        model.addMaximize(funcaoObjetivoExp);

        //--------------Restrições--------------
        //Quantidade
        for (int i = 0; i < n; i++) {
            IloLinearNumExpr restricaoQtd = model.linearNumExpr();
            for (int j = 0; j < bags; j++) {
                restricaoQtd.addTerm(1, x[i][j]);
            }
            model.addLe(restricaoQtd, limiteItens);
        }

        //Peso
        for (int j = 0; j < bags; j++) {
            IloLinearNumExpr restricaoPeso = model.linearNumExpr();
            for (int i = 0; i < n; i++) {
                restricaoPeso.addTerm(peso[i], x[i][j]);
            }
            model.addLe(restricaoPeso, limitePeso);
        }

        //Volume
        for (int j = 0; j < bags; j++) {
            IloLinearNumExpr restricaoVolume = model.linearNumExpr();
            for (int i = 0; i < n; i++) {
                restricaoVolume.addTerm(volume[i], x[i][j]);
            }
            model.addLe(restricaoVolume, limiteVolume);
        }

        //--------------Solving--------------
        //model.addMIPStart(vetVars, vetValues, IloCplex.MIPStartEffort.Auto);
        if (model.solve()) {
            for (int j = 0; j < bags; j++) {
                System.out.println("Mochilha [" + j + "]:");
                for (int i = 0; i < n; i++) {
                    if (model.getValue(x[i][j]) > 0) {
                        System.out.println("Item " + i + ": " + model.getValue(x[i][j]));
                    }
                }
            }
            double fo = model.getObjValue();
            System.out.println("\tRetorno: \t" + fo);
        } else {
            System.out.println("Deu bosta!");
        }
    }
}
