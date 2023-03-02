/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsflexible;

import java.util.Random;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;

public class operation
{
    int pedido;
    int operation;
    HashMap<Integer, Double> SetMachineInicial;
    HashMap<Integer, Double> SetMachine;
    double startTime;
    double finalTime;
    double processingTime;
    int maquina;
    operation anteriorRuta;
    operation siguieteRuta;
    operation anteriorM;
    operation siguientM;
    int arcos;
    boolean realizado;
    static int totalPedidos;
    int posicionMatrizF;
    double[] colas;
    boolean estaRuta;
    boolean halloReasignacion;
    boolean inicial;
    boolean fin;
    boolean acoH;
    int qw;
    double q;
    HashMap<Integer, Double> fermoMaquina;
    
    public operation(final int pedido, final int operation, final HashMap SetMachine) {
        this.SetMachineInicial = new HashMap<Integer, Double>();
        this.startTime = 0.0;
        this.finalTime = 0.0;
        this.processingTime = 0.0;
        this.arcos = -1;
        this.realizado = false;
        this.colas = new double[jsflexible.operation.totalPedidos];
        this.estaRuta = false;
        this.inicial = false;
        this.fin = false;
        this.acoH = false;
        this.qw = 0;
        this.pedido = pedido;
        this.operation = operation;
        this.SetMachineInicial = (HashMap<Integer, Double>)SetMachine;
        this.setMachine();
    }
    
    public void addOperacionSiguiente(final operation operacionSiguiete) {
        this.siguientM = operacionSiguiete;
    }
    
    public void addOperacionAnterior(final operation operacionAnterior) {
        this.anteriorM = operacionAnterior;
    }
    
    public void setAnteriorRuta(final operation anteriorRuta) {
        this.anteriorRuta = anteriorRuta;
    }
    
    public void setSiguieteRuta(final operation siguieteRuta) {
        this.siguieteRuta = siguieteRuta;
    }
    
    public void operar() {
        this.setStartTime();
        this.setProcessingTime(this.maquina);
        this.finalTime = this.startTime + this.processingTime;
        this.arcos = Math.max(this.anteriorM.getArcos(), this.anteriorRuta.getArcos()) + 1;
    }
    
    public double getStartTime() {
        return this.startTime;
    }
    
    public double getFinalTime() {
        return this.finalTime;
    }
    
    public double getProcessingTime() {
        return this.processingTime;
    }
    
    public void setProcessingTime(final int machine) {
        this.maquina = machine;
        this.processingTime = this.SetMachine.get(machine);
    }
    
    public int getPedido() {
        return this.pedido;
    }
    
    public int getArcos() {
        return this.arcos;
    }
    
    public boolean isRealizado() {
        return this.realizado;
    }
    
    public void setRealizado(final boolean realizado) {
        this.realizado = realizado;
    }
    
    public int getMaquinaSelecionada() {
        return this.maquina;
    }
    
    public int getOperation() {
        return this.operation;
    }
    
    public void setStartTime() {
        this.startTime = Math.max(this.anteriorM.getFinalTime(), this.anteriorRuta.getFinalTime());
    }
    
    public void setStartTime1(final double inicio) {
        this.startTime = inicio;
    }
    
    public void setArcos(final int arcos) {
        this.arcos = arcos;
    }
    
    public double[] getColas() {
        return this.colas;
    }
    
    public void setColas(final double[] colas) {
        this.colas = colas;
    }
    
    public void hallarColas(final LinkedList<rutaCritica> rutaC) {
        final operation opSiguiente = this.siguientM;
        final double horaInicioSiguienteCalculada = this.finalTime;
        if (horaInicioSiguienteCalculada == opSiguiente.getStartTime() && opSiguiente.estaRuta) {
            this.estaRuta = true;
            final rutaCritica r = new rutaCritica(this, opSiguiente);
            rutaC.add(r);
        }
        if (this.siguieteRuta.getStartTime() == this.finalTime && this.siguieteRuta.estaRuta) {
            final rutaCritica r = new rutaCritica(this, this.siguieteRuta);
            rutaC.add(r);
            this.estaRuta = true;
        }
    }
    
    public void setFinalTime() {
        this.finalTime = this.startTime + this.processingTime;
    }
    
    public boolean isEstaRuta() {
        return this.estaRuta;
    }
    
    public boolean isHalloReasignacion() {
        return this.halloReasignacion;
    }
    
    public void setHalloReasignacion(final boolean halloReasignacion) {
        this.halloReasignacion = halloReasignacion;
    }
    
    public operation getAnteriorM() {
        return this.anteriorM;
    }
    
    public operation getSiguientM() {
        return this.siguientM;
    }
    
    public operation getAnteriorRuta() {
        return this.anteriorRuta;
    }
    
    public operation getSiguieteRuta() {
        return this.siguieteRuta;
    }
    
    public operation clonar() {
        final operation op = new operation(this.pedido, this.operation, this.SetMachineInicial);
        op.setPosMatriz(this.posicionMatrizF);
        return op;
    }
    
    @Override
    public String toString() {
        final LinkedList<Double> cola = new LinkedList<Double>();
        for (int i = 0; i < this.colas.length; ++i) {
            cola.add(this.colas[i]);
        }
        return "{" + this.pedido + "-" + this.operation + "}";
    }
    
    public void setInicial(final boolean inicial) {
        this.inicial = inicial;
    }
    
    public void setFin(final boolean fin) {
        this.fin = fin;
    }
    
    public boolean isInicial() {
        return this.inicial;
    }
    
    public boolean isFin() {
        return this.fin;
    }
    
    public boolean verificar() {
        return this.anteriorRuta.isRealizado() & this.anteriorM.isRealizado();
    }
    
    public void selecionarMaquina(final int machine) {
        this.maquina = machine;
    }
    
    public HashMap<Integer, Double> getSetMachine() {
        return this.SetMachine;
    }
    
    public void setPosMatriz(final int pos) {
        this.posicionMatrizF = pos;
    }
    
    public int getPosicionMatrizF() {
        return this.posicionMatrizF;
    }
    
    public boolean isAcoH() {
        return this.acoH;
    }
    
    public void setAcoH(final boolean acoH) {
        this.acoH = acoH;
    }
    
    public void setEstaRuta(final boolean estaRuta) {
        this.estaRuta = estaRuta;
    }
    
    public void fermonaMaquina() {
        this.fermoMaquina = new HashMap<Integer, Double>();
    }
    
    public double getQ() {
        return this.q;
    }
    
    public int getQw() {
        return this.qw;
    }
    
    public void setQw() {
        if (this.siguieteRuta.getSetMachine() != null) {
            this.qw = this.siguieteRuta.getQw() + this.siguieteRuta.promedioTiempo();
        }
    }
    
    public int promedioTiempo() {
        final int promedio = 0;
        int suma = 0;
        int contador = 0;
        for (final Map.Entry<Integer, Double> set : this.SetMachine.entrySet()) {
            if (set.getValue() > suma) {
                suma = (int)(set.getValue() + 0.0);
            }
            ++contador;
        }
        return suma;
    }
    
    void setMachine() {
        this.SetMachine = new HashMap<Integer, Double>();
        if (this.SetMachineInicial != null) {
            for (final Map.Entry<Integer, Double> entry : this.SetMachineInicial.entrySet()) {
                final int maquina = entry.getKey();
                final double tiempo = entry.getValue();
                int a = (int)(tiempo - 0.2 * tiempo);
                int b = (int)(tiempo + 0.2 * tiempo);
                final Random r = new Random();
                if (b == 0 & a == 0) {
                    b = 2;
                    a = 1;
                }
                final int tiempoP = r.nextInt(a, b);
                this.SetMachine.put(maquina, (double)tiempoP);
            }
        }
    }
    
    public operation clonarEstocastico() {
        final operation op = new operation(this.pedido, this.operation, this.SetMachine);
        op.SetMachine = this.SetMachine;
        op.setPosMatriz(this.posicionMatrizF);
        op.setProcessingTime(this.maquina);
        op.setStartTime1(this.startTime);
        op.setFinalTime();
        return op;
    }
}