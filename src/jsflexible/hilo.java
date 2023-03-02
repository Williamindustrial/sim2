/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jsflexible;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrp.LecturaDatos;
import vrp.LocalSearch;

/**
 *
 * @author lizbe
 */
class hilo extends Thread {

    operation opInicial;
    operation opFinal;
    int[] numeroOperaciones;
    LinkedList<solution> soluciones;
    LecturaDatos lecturer;
    String directionvrpfile;
    LinkedList<TransfomaGrafo> vecindarioInicial;
    LinkedList<Integer> x;
    LinkedList<Integer> y;
    LinkedList<Integer> DueDate;
    LinkedList<Integer> customer;
    LinkedList<Integer> Demand;
    int neib;
    boolean initial;
    int numeroIteraciones;
    static int bestLocal = 100000000;
    static solution bestSolLocal;
    int mtxDistancia[][];

    public hilo(operation opInicial, operation opFinal, int[] numeroOperaciones, LecturaDatos lecturer, String directionvrpfile, LinkedList<TransfomaGrafo> vecindarioInicial, LinkedList<Integer> x, LinkedList<Integer> y, LinkedList<Integer> DueDate, LinkedList<Integer> customer, LinkedList<Integer> Demand, int neib, boolean initial, int numeroIteraciones, LinkedList soluciones, int mtxDistancia[][]) {
        this.opInicial = opInicial;
        this.opFinal = opFinal;
        this.numeroOperaciones = numeroOperaciones;
        this.lecturer = lecturer;
        this.directionvrpfile = directionvrpfile;
        this.vecindarioInicial = vecindarioInicial;
        this.x = x;
        this.y = y;
        this.DueDate = DueDate;
        this.customer = customer;
        this.Demand = Demand;
        this.neib = neib;
        this.initial = initial;
        this.numeroIteraciones = numeroIteraciones;
        this.soluciones = soluciones;
        this.mtxDistancia = mtxDistancia;
    }

    public void ejecutar(final TransfomaGrafo vecino, final int[][] mtxDistancia) throws IOException {
        vecino.realizar();
        solution initial = new solution(vecino.getPedidosClonados(), vecino.getOrdenFinalMaquinas(), this.numeroOperaciones, vecino.getOrden(), this.opInicial, this.opFinal, 0);
        initial.solucionar();
        double promedio = 0.0;
        double suma = 0.0;
        int it = 15;
        double[] valor = new double[it];
        LinkedList<solution> listsi = new LinkedList<solution>();
        for (int i = 0; i < it; ++i) {
            solution si = new solution(vecino.getPedidosClonados(), vecino.getOrdenFinalMaquinas(), this.numeroOperaciones, vecino.getOrden(), this.opInicial, this.opFinal, 0);
            si.solucionar();
            double solcurrently = si.calcularCMRAX(this.directionvrpfile, false, (LinkedList) this.x, (LinkedList) this.y, (LinkedList) this.DueDate, (LinkedList) this.customer, (LinkedList) this.Demand, 0, true, 0, mtxDistancia);
            suma += solcurrently;
            valor[i] = solcurrently;
        }
        double media = suma / it;
        double varianza = 0.0;
        double desviacion = 0.0;
        for (int j = 0; j < it; ++j) {
            double rango = Math.pow(valor[j] - media, 2.0);
            varianza += rango;
        }
        varianza /= it;
        desviacion = Math.sqrt(varianza);
        double ox = desviacion / Math.sqrt(it);
        double valorT = 2.1448;
        double estimacion = media + valorT * ox;
        initial.setMeanCRMAC(estimacion);
        initial.setListsol((LinkedList) listsi);
        initial.setValor(valor);
        this.soluciones.add(initial);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(0);
        } catch (InterruptedException ex) {
            Logger.getLogger(hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < vecindarioInicial.size(); i++) {
            try {
                ejecutar(vecindarioInicial.get(i), mtxDistancia);
            } catch (IOException ex) {
                Logger.getLogger(hilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    LinkedList<solution> getSoluciones() {
        return soluciones;
    }

    public static int getBestLocal() {
        return bestLocal;
    }

    public static solution getBestSolLocal() {
        return bestSolLocal;
    }

}
