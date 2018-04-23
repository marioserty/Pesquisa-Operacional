/*
    Primeira implmentacao do CPLEX feito em aula para o problema de soja e milho.
*/
package cplex;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class CPLEX {

    public static void main(String[] args) throws IloException {
        new CPLEX();
    }

    public CPLEX() throws IloException {
        IloCplex model = new IloCplex();

        //variaveis de decisao
        IloIntVar milho = model.intVar(0, 99999999);
        IloIntVar soja = model.intVar(0, 99999999);
       

        //função objetivo
        IloLinearNumExpr funcaoObjetivoExp = model.linearNumExpr();
        funcaoObjetivoExp.addTerm(280, milho);
        funcaoObjetivoExp.addTerm(300, soja);

        model.addMaximize(funcaoObjetivoExp);

        //restrição de carga
        IloLinearNumExpr restricaoCarga = model.linearNumExpr();
        restricaoCarga.addTerm(80, milho);
        restricaoCarga.addTerm(50, soja);

        model.addLe(restricaoCarga, 400); // LE menor igual

        //restrição de custo
        IloLinearNumExpr restricaoFinanceira = model.linearNumExpr();
        restricaoFinanceira.addTerm(50, milho);
        restricaoFinanceira.addTerm(70, soja);

        model.addLe(restricaoFinanceira, 350); // LE menor igual

        //restricao de disponibilidade de soja
        IloLinearNumExpr restricaoMercado = model.linearNumExpr();
        restricaoMercado.addTerm(1, soja);

        model.addLe(restricaoMercado, 4);
       
        //solução de partida
       
        IloIntVar[] vetVars = new IloIntVar[2];
        double[] vetValues = new double[2];
        vetVars[0] = milho;
        vetVars[1] = soja;
        vetValues[0] = 2;
        vetValues[1] = 2;
       
        model.addMIPStart(vetVars, vetValues, IloCplex.MIPStartEffort.Auto);
        if (model.solve()) {
            double sacosDeMilho = model.getValue(milho);
            double sacosDeSoja = model.getValue(soja);
            double funcaoObjetivo =model.getObjValue();
            System.out.println("quantidade de milho: " + sacosDeMilho);
            System.out.println("quantidade de soja: " + sacosDeSoja);
            System.out.println("Função objetivo: " + funcaoObjetivo);
            System.out.println("");
        } else {
            System.out.println("Deu bosta!");
        }

    }
}

