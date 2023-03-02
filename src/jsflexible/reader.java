/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsflexible;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import vrp.LecturaDatos;
import vrp.LocalSearch;
import vrp.ReadData;

/**
 *
 * @author willi
 */
public class reader {

    /**
     * Factor de flexibilidad
     */
    double factorFlexibilidad;
    /**
     * Dirección archivo de origen
     */
    String file;
    /**
     * Dirección a guardar archivos resultantes
     */
    String RutaSolucion;

    /**
     * Criterio a optimizar
     */
    int optimizador;
    /**
     * Retorna el número total de operaciones
     */
    int numeroOp = 0;
    /**
     * Vector que contiene el número de operaciones de cada pedido
     */
    int[] numeroOperaciones;
    /**
     * Contiene el orden de las maquinas
     */
    LinkedList<LinkedList> listaMaquinas;
    /**
     * Operación final e inicial
     */
    operation opInicial, opFinal;
    /**
     * Matriz de las feromonas
     */
    feromonas mtx;
    /**
     * Hora de inicio del algoritmo
     */
    long inicio;
    /**
     * Límite superior de feromonas
     */
    double li;
    /**
     * Límite superior de feromonas
     */
    double ls;
    /**
     * Valor inicial de las feromonas;
     */
    double vi;
    /**
     * Factor de evaporación
     */
    double initial;

    /**
     * Constructor del lector
     *
     * @param file Dirección del archivo a leer
     * @param direccionGuardarSolucion Dirección del archivo a guardar
     * @param directionvrpfile Dirección donde leer el archivo vrp
     * @param perturbaciones Número de perturbaciones
     */
    public reader(String file, String direccionGuardarSolucion, String directionvrpfile, double initial, int perturbaciones) {
        this.file = file;
        this.RutaSolucion = direccionGuardarSolucion;
        factorFlexibilidad = 1.3;
        valorBest = 10000000;
        this.directionvrpfile = directionvrpfile;
        this.perturbacion = perturbaciones;
        this.initial = initial;
    }

    /**
     * Total de pedidos
     */
    int numeroPedidos = 0;
    /**
     * Número de maquinas del schedulling
     */
    int numeroMaquinas = 0;
    /**
     * Maximo número de operaciones por pedidos
     */
    int maximoOPeracionesxPedidos = 0;
    /**
     * Estructura que guarda los pedidos La posición [i] contiene el pedido
     */
    LinkedList<pedido> pedidos = new LinkedList<>();
    LinkedList<pedido> pedidos2 = new LinkedList<>();
    /**
     * El valor best en static
     */
    static double valorBest;
    /**
     * Soluciones grafica
     */

    LinkedList<HashMap> soluciones = new LinkedList<>();
    /**
     * Cantidad de perturbaciones
     */
    int perturbacion;

    /**
     * Lee el archivo
     *
     * @throws FileNotFoundException: No se encontro el archivo
     * @throws IOException: El archivo no es correcto
     * @throws Exception: El archivo no es correcto
     */
    public void flexible() throws FileNotFoundException, IOException, Exception {
        LinkedList<Integer> di = new LinkedList<>();
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String bfRead;
        int numeroMatriz = 0;
        double[][] matric = new double[numeroPedidos + 1][numeroPedidos + 1];
        int numeroVecesMatriz = 0;
        int pos = 1;
        ArrayList<Integer> numeradores = new ArrayList();
        int numeroGuia = 0;
        try {
            bf = new BufferedReader(new FileReader(file));
            int numeroLineas = 0;
            int matrix = 0;
            while ((bfRead = bf.readLine()) != null) {
                StringTokenizer lineasConEspacios = new StringTokenizer(bfRead);
                String cadenaLetras[] = bfRead.split(" ");
                int cadenaNumeros[] = new int[cadenaLetras.length];
                if (numeroLineas == 0) {
                    numeroPedidos = Integer.parseInt(cadenaLetras[0]);
                    operation.totalPedidos = numeroPedidos;
                    numeroMaquinas = Integer.parseInt(cadenaLetras[1]);
                    numeroLineas++;
                } else {
                    for (int w = 0; w < numeroPedidos; w++) {
                        cadenaLetras = bfRead.split(" ");
                        cadenaNumeros = new int[cadenaLetras.length];
                        int numeroCaracteres = 0;
                        int numeroOperaciones = Integer.parseInt(cadenaLetras[0]);
                        numeroOp += numeroOperaciones;
                        if (maximoOPeracionesxPedidos < numeroOperaciones) {
                            maximoOPeracionesxPedidos = numeroOperaciones;
                        }
                        numeroCaracteres++;
                        LinkedList<operation> operaciones = new LinkedList<>();
                        double valorMtotal = 0;
                        for (int i = 0; i < numeroOperaciones; i++) {
                            double tiempoMaximo = 0;
                            int numeroMaquinasO = Integer.parseInt(cadenaLetras[numeroCaracteres]);
                            numeroCaracteres++;
                            HashMap<Integer, Double> tiempos = new HashMap<>();
                            for (int j = 0; j < numeroMaquinasO; j++) {
                                int clave = Integer.parseInt(cadenaLetras[numeroCaracteres]);
                                numeroCaracteres++;
                                double valor = Double.parseDouble(cadenaLetras[numeroCaracteres]);
                                tiempos.put(clave - 1, valor);
                                numeroCaracteres++;
                                tiempoMaximo = valor + tiempoMaximo;
                            }
                            operation op = new operation(w, i, tiempos);
                            double agregar = tiempoMaximo / tiempos.size();
                            valorMtotal = valorMtotal + agregar;
                            op.setPosMatriz(pos);
                            operaciones.add(op);
                            pos++;
                        }
                        int v = (int) Math.round(factorFlexibilidad * valorMtotal);
                        di.add(v);
                        //pedidos.add(operaciones);
                        pedido p = new pedido(numeroOperaciones, operaciones);
                        bfRead = bf.readLine();
                        pedidos2.add(p);
                        pedidos2.getLast().setdueDate(Double.valueOf(v));
                    }
                    matrix++;
                }

            }
            for (int i = 0; i < pedidos.size(); i++) {
                if (i < pedidos.size() * 0.2) {
                    pedidos.get(i).setWi(4);
                } else if (i >= pedidos.size() * 0.2 & i < pedidos.size() * 0.8) {
                    pedidos.get(i).setWi(2);
                } else {
                    pedidos.get(i).setWi(1);
                }
            }
            numeroLineas++;
            System.out.println("Due date " + di);

        } catch (NumberFormatException e) {
            System.out.println("NO SE ENCONTRO ARCHIVO.........");
            e.printStackTrace();
        }
        opInicial = new operation(-1, -1, null);
        opFinal = new operation(pedidos.size(), maximoOPeracionesxPedidos + 1, null);
        System.out.println("FINAL" + opFinal);
        System.out.println("OPINICIAL" + opInicial);
        readTSP(neib, directionvrpfile);
    }

    /**
     * Seleciona la maquina de cada operación
     */
    public void selecionarMaquina() {
        numeroOperaciones = new int[pedidos.size()];
        for (int i = 0; i < pedidos.size(); i++) {
            pedido p = pedidos.get(i);
            for (int j = 0; j < p.getOperaciones().size(); j++) {
                operation op = p.getOperaciones().get(j);
                double menorTiempo = 100000000;
                for (Map.Entry<Integer, Double> entry : op.getSetMachine().entrySet()) {
                    if (menorTiempo > entry.getValue()) {
                        op.selecionarMaquina(entry.getKey());
                        menorTiempo = entry.getValue();
                    }
                }
                if (op.equals(pedidos.get(i).getOperaciones().getFirst())) {
                    op.setAnteriorRuta(opInicial);
                } else if (op.equals(pedidos.get(i).getOperaciones().getLast())) {
                    operation ant = pedidos.get(i).getOperaciones().get(j - 1);
                    ant.setSiguieteRuta(op);
                    op.setAnteriorRuta(pedidos.get(i).getOperaciones().get(j - 1));
                    op.setSiguieteRuta(opFinal);
                } else {
                    operation ant = pedidos.get(i).getOperaciones().get(j - 1);
                    ant.setSiguieteRuta(op);
                    op.setAnteriorRuta(pedidos.get(i).getOperaciones().get(j - 1));
                }
            }
            numeroOperaciones[i] = pedidos.get(i).getNumerooperaciones();
            //numeroOp+= pedidos.get(i).getNumerooperaciones();
        }
    }

    /**
     * Genera el orden inicial de las operaciones
     *
     * @return orden inicial de las operaciones
     */
    public LinkedList generarGrupoDeOperaciones() {
        selecionarMaquina();
        LinkedList<operation> listaOp = new LinkedList<>();
        LinkedList<Double> due = new LinkedList<>();
        LinkedList<Integer> posicionesDuedate = new LinkedList<>();
        listaMaquinas = new LinkedList<>();
        for (int i = 0; i < numeroMaquinas; i++) {
            LinkedList<operation> listaM = new LinkedList<>();
            listaMaquinas.add(listaM);
        }
        for (int i = 0; i < pedidos.size(); i++) {
            due.add(pedidos.get(i).getDueDate());
        }
        for (int i = 0; i < pedidos.size(); i++) {
            double menor = 1000000000;
            int posicion = 0;
            for (int j = 0; j < due.size(); j++) {
                if (menor > due.get(j)) {
                    menor = due.get(j);
                    posicion = j;
                }
            }
            posicionesDuedate.add(posicion);
            due.set(posicion, 10000000.0);
        }

        for (int i = 0; i < maximoOPeracionesxPedidos; i++) {
            for (int j = 0; j < pedidos.size(); j++) {
                if (i < pedidos.get(posicionesDuedate.get(j)).getOperaciones().size()) {
                    operation add = pedidos.get(posicionesDuedate.get(j)).getOperaciones().get(i);
                    int maquina = add.getMaquinaSelecionada();
                    listaOp.add(add);
                    if (listaMaquinas.get(maquina).isEmpty()) {
                        add.addOperacionAnterior(opInicial);
                    } else if (i == pedidos.get(posicionesDuedate.get(j)).getOperaciones().size() - 1) {
                        add.addOperacionAnterior((operation) listaMaquinas.get(maquina).getLast());
                        operation ant = (operation) listaMaquinas.get(maquina).getLast();
                        ant.addOperacionSiguiente(add);
                        add.addOperacionSiguiente(opFinal);
                    } else {
                        add.addOperacionAnterior((operation) listaMaquinas.get(maquina).getLast());
                        operation ant = (operation) listaMaquinas.get(maquina).getLast();
                        ant.addOperacionSiguiente(add);
                    }
                    listaMaquinas.get(maquina).add(add);
                }
            }
        }
        return listaOp;
    }

    public LinkedList diversificacion2(solution sw, int n) {
        LinkedList<TransfomaGrafo> vecinario = new LinkedList<>();
        LinkedList<TransfomaGrafo> vecindarioCompleto = new LinkedList<>();
        sw.colas(0);
        sw.algoritmo(1);
        vecindarioCompleto = sw.getVecindarioCompleto();
        vecinario = sw.getVecindario();
        Random r = new Random();
        int a = r.nextInt(((n) + 1));
        int posicion = r.nextInt(vecinario.size());
        TransfomaGrafo vecino = (TransfomaGrafo) vecinario.get(posicion);
        vecino.realizar();
        for (int i = 0; i < n; i++) {
            //solution si = new solution(vecino.getPedidosClonados(), vecino.getOrdenFinalMaquinas(), numeroOperaciones, CambiosTiemposXMaquina, direcciónGuardarArchivos, vecino.getOrden(), opInicial, opFinal);
            solution si = new solution(vecino.getPedidosClonados(), vecino.getOrdenFinalMaquinas(), numeroOperaciones, vecino.getOrden(), opInicial, opFinal, optimizador);
            si.solucionar();
            si.colas(0);
            si.algoritmo(0);
            si.algoritmo(1);
            vecindarioCompleto = si.getVecindarioCompleto();
            vecinario = si.getVecindario();
            a = r.nextInt(((n + i) + 1));
            posicion = r.nextInt(vecinario.size());
            vecino = (TransfomaGrafo) vecinario.get(posicion);
            vecino.realizar();
        }
        return vecinario;
    }

    /**
     * Clonar la estructura de datos que contiene la información
     *
     * @param pedidos
     * @return estructura de datos clonada
     */
    public LinkedList<pedido> clonarLista(LinkedList<pedido> pedidos) {
        LinkedList<pedido> pedidoClonado = new LinkedList<>();
        for (pedido Pedido : pedidos) {
            pedidoClonado.add(Pedido.clonar1());
        }
        return pedidoClonado;
    }

    HashMap<Double, Double> valoresTime = new HashMap<>();

    void guardarSoluciones(double time, double solution) throws IOException {
        valoresTime.put(time, solution);
        guardarSol(RutaSolucion, "Time");
    }

    public void setStocasticos() {
        for (int i = 0; i < pedidos2.size(); i++) {
            pedido p = pedidos2.get(i).clonar1();
            pedidos.add(p);
        }
    }

    String directionvrpfile;

    void slocal(double it, int numeroH, double alfa, double beta, int w, String localizacionVRP) throws Exception {
        flexible();
        setStocasticos();
        mtxDistance();
        double best = 1000000000.0;
        inicio = System.nanoTime();
        double demor = 0;
        double bestH = 1000000000;
        hormiga b = null;
        hormiga as = null;
        LecturaDatos l = new LecturaDatos();
        l.readTSP(0, 1, directionvrpfile);
        ReadData bestR = null;
        LocalSearch bl = null;
        for (int i = 0; i < 1; i++) {
            LinkedList<operation> listaOperation = generarGrupoDeOperaciones();
            hormiga h = new hormiga(clonarLista(pedidos), listaOperation, numeroPedidos, maximoOPeracionesxPedidos, numeroMaquinas, numeroOp, opInicial, opFinal, optimizador, mtx, directionvrpfile);
            l.initialSol(localizacionVRP, h.getCi(),mtxDistancia);
            ReadData r = l.getR();
            r.VND(i, 0);
            bl = r.getBestSol();
            if (bl.getCRMAX() < bestH) {
                System.out.println(bestH);
                bestH = bl.getCRMAX();
                b = h;
                if (bl.getCRMAX() < best) {
                    best = bl.getCRMAX();
                    as = b;
                    bestR = r;
                    bl.write(RutaSolucion, demor);
                    as.writeMono(RutaSolucion, "Initial");
                }
            }
        }
        System.out.println("Local: " + bestH + "Best: " + best);
        long fin = System.nanoTime();
        demor = (fin - inicio) * 1.0e-9;

        opInicial.setInicial(true);

        solution solutionBest = VNS2(b, 3, demor, bl, 1);
        guardarSol(RutaSolucion, "Tiempos");

    }

    solution best = null;
    int contadora = 10;

    LecturaDatos lecturer = new LecturaDatos();

    solution VNS2(hormiga h, double it, double tActual, LocalSearch initial, int n) throws IOException {

        lecturer.readTSP(0, 1, directionvrpfile);
        solution inicial = new solution(h.getPedidos(), h.getOrdenMaquinas(), numeroOperaciones, h.getOrdenSolutionF(), opInicial, opFinal, optimizador);
        inicial.solucionar();
        inicial.colas(0);
        inicial.algoritmo(0);
        //inicial.colas(estadistica, n, 2, 0, false);
        LinkedList<TransfomaGrafo> vecindario = inicial.getVecindario();
        TransfomaGrafo vecino;
        TransfomaGrafo vecinoLocal;
        solution solucionBest = inicial;
        inicial.calcularCMRAX(RutaSolucion, true, x, y, DueDate, customer, Demand, neib, true, numeroIteraciones,mtxDistancia);
        try {
            vecino = inicial.getVecindario().getFirst();
            vecinoLocal = inicial.getVecindario().getFirst();
        } catch (Exception e) {
            vecino = inicial.getVecindarioCompleto().getFirst();
            vecinoLocal = inicial.getVecindarioCompleto().getFirst();
        }

        vecino.realizar();
        TransfomaGrafo vecinoBest = vecino;
        LocalSearch mejorVRP = null;
        double bestLocal = 10000000;
        System.out.println(inicial.getValorLocal());
        solution si = inicial;
        solution bi = si;
        System.out.println("inicia");
        double demor = 0;
        int i = 0;
        boolean m = false;
        inicio = System.nanoTime();
        double bestH;
        LocalSearch solLocalr = initial;
        solution locals = inicial;
        while (i < n) {
            Random numAleatorio = new Random();
            int veci = 0;
            int veciIndez = 0;
            while (demor < (60 * 60)) {
                LinkedList<Integer> LV = new LinkedList<>();
                LV.add(0);
                int bl = 100000000;
                //int veciIndez = 0;
                //int veci = 0;
                veciIndez = numAleatorio.nextInt(LV.size());
                veci = LV.get(veciIndez);
                int total = 0;

                bestLocal = 1000000;
                while (!LV.isEmpty()) {
                    boolean mejoro = false;
                    // System.out.println("ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
                    LinkedList<solution> s = excecuteThreads(vecindario, 8);
                    if (veci == 2) {
                        LocalSearch CRMAXa = locals.vencidario3();
                        int CRMAX = CRMAXa.getCRMAX();
                        if (CRMAX < bestLocal) {
                            mejoro = true;
                            bestLocal = CRMAX;
                            si = locals;
                            System.out.println("best: " + valorBest + "--local: " + bestLocal + "Vecino: " + veci);
                            if (valorBest > CRMAX) {
                                System.out.println("best");
                                long fin = System.nanoTime();
                                demor = (fin - inicio) * 1.0e-9;
                                valorBest = CRMAX;
                                vecinoBest = vecino;
                                si.writeMono(RutaSolucion, "Best", demor);
                                best = si;
                                solucionBest = si;
                                long fini = System.nanoTime();
                                tActual = (fini - inicio) * 1.0e-9;
                                si.writeMono(RutaSolucion, "Best", tActual);
                                guardarSoluciones(tActual, valorBest);
                            }
                        }
                    } else {
                        for (int j = 0; j < s.size(); j++) {
                            //System.out.println(s.size());
                            try {
                                si = s.get(j);
                            } catch (Exception e) {
                                double CRMAX = si.getMeanCRMAC();
                                total++;
                                float aaa = total / s.size();
                                //System.out.println("total:" + total + " tamaño" + CRMAX);
                            }

                            double CRMAX = si.getMeanCRMAC();
                            if (CRMAX < bestLocal) {
                                mejoro = true;
                                bestLocal = CRMAX;
                                locals = si;
                                System.out.println("best: " + valorBest + "--local: " + bestLocal + "Vecino: " + veci);
                                if (valorBest > CRMAX) {
                                    long fin = System.nanoTime();
                                    demor = (fin - inicio) * 1.0e-9;
                                    valorBest = CRMAX;
                                    vecinoBest = vecino;
                                    //si.writeMono(RutaSolucion, "Best", demor);
                                    best = si;
                                    solucionBest = si;
                                    long fini = System.nanoTime();

                                    tActual = (fini - inicio) * 1.0e-9;
                                   // si.writeMono(RutaSolucion, "Best", tActual);
                                    guardarSoluciones(tActual, valorBest);
                                }
                            }
                        }
                    }
                    if (mejoro) {
                        LV.clear();
                        LV.add(0);
                        locals.colas(0);
                        if (veci < 2) {
                            locals.algoritmo(veci);
                            double a = si.getValorLocal();
                            System.out.println("best: " + valorBest + "--local: " + bestLocal + "Vecino: " + veci);
                            vecindario = si.getVecindario();
                        } else {
                            veciIndez = numAleatorio.nextInt(LV.size());
                            veci = LV.get(veciIndez);
                            System.out.println("Change neibor to " + veci);
                        }
                    } else {

                        LV.remove(veciIndez);
                        if (LV.size() > 0) {
                            if (veci < 2) {
                                veciIndez = numAleatorio.nextInt(LV.size());
                                veci = LV.get(veciIndez);
                                System.out.println("Change neibor to " + veci);
                                locals.algoritmo(veci);
                                vecindario = locals.getVecindario();
                            } else {
                                veciIndez = numAleatorio.nextInt(LV.size());
                                veci = LV.get(veciIndez);
                                System.out.println("Change neibor to " + veci);
                            }
                        }
                    }
                }

                System.out.println("reinicio");
                Random ri = new Random();
                vecindario = diversificacion2(locals, 2);
                bestLocal = 1000000000;
                i++;
                long fin = System.nanoTime();
                demor = (fin - inicio) * 1.0e-9;
            }
            // vecindario = tabu(vecinoBest, tiempoTtal, tActual, estadistica, n, ww);
            System.out.println("Sale");
            // Empezo nuevo
            bestLocal = si.getValorLocal();
        }
        return solucionBest;
    }

    LinkedList<TransfomaGrafo> movPenalizados = new LinkedList<>();

    void guardarSol(String ruta, String name) throws IOException {
        String rutaP = ruta;
        ruta = ruta + File.separator + name + ".txt";
        FileWriter escribir = new FileWriter(ruta);
        BufferedWriter escritor = new BufferedWriter(escribir);
        for (Map.Entry<Double, Double> entry : valoresTime.entrySet()) {
            escritor.write(entry.getKey() + " " + entry.getValue());
            escritor.newLine();
        }
        escritor.close();
        escribir.close();
    }

    @SuppressWarnings("empty-statement")
    public LinkedList excecuteThreads(LinkedList vecindario, int numerohilos) {
        LinkedList<solution> soluciones = new LinkedList<>();
        int tamañoVecindadio = vecindario.size();
        double bb = (double) tamañoVecindadio / (double) numerohilos;
        int numeroN = (int) Math.ceil(bb);
        int contador = 0;
        Thread t1 = new Thread();
        Thread t2 = new Thread();
        Thread t3 = new Thread();
        Thread t4 = new Thread();
        Thread t5 = new Thread();
        Thread t6 = new Thread();
        Thread t7 = new Thread();
        Thread t8 = new Thread();
        for (int i = 0; i < numerohilos; i++) {
            LinkedList<TransfomaGrafo> vecindarios = new LinkedList<>();

            for (int j = 0; j < numeroN; j++) {
                if (contador < tamañoVecindadio) {
                    vecindarios.add((TransfomaGrafo) vecindario.get(contador));
                }
                contador++;
            }
            if (!vecindarios.isEmpty()) {
                if (i == 0) {
                    t1 = new hilo(opInicial, opFinal, numeroOperaciones, lecturer, directionvrpfile, vecindarios, x, y, DueDate, customer, Demand, neib, true, numeroIteraciones, soluciones,mtxDistancia);
                    t1.start();

                }
                if (i == 1) {
                    t2 = new hilo(opInicial, opFinal, numeroOperaciones, lecturer, directionvrpfile, vecindarios, x, y, DueDate, customer, Demand, neib, true, numeroIteraciones, soluciones,mtxDistancia);
                    t2.start();
                }
                if (i == 2) {
                    t3 = new hilo(opInicial, opFinal, numeroOperaciones, lecturer, directionvrpfile, vecindarios, x, y, DueDate, customer, Demand, neib, true, numeroIteraciones, soluciones,mtxDistancia);
                    t3.start();
                }
                if (i == 3) {
                    t4 = new hilo(opInicial, opFinal, numeroOperaciones, lecturer, directionvrpfile, vecindarios, x, y, DueDate, customer, Demand, neib, true, numeroIteraciones, soluciones,mtxDistancia);
                    t4.start();
                }
                if (i == 4) {
                    t5 = new hilo(opInicial, opFinal, numeroOperaciones, lecturer, directionvrpfile, vecindarios, x, y, DueDate, customer, Demand, neib, true, numeroIteraciones, soluciones,mtxDistancia);
                    t5.start();
                }
                if (i == 5) {
                    t6 = new hilo(opInicial, opFinal, numeroOperaciones, lecturer, directionvrpfile, vecindarios, x, y, DueDate, customer, Demand, neib, true, numeroIteraciones, soluciones,mtxDistancia);
                    t6.start();
                }
                if (i == 6) {
                    t7 = new hilo(opInicial, opFinal, numeroOperaciones, lecturer, directionvrpfile, vecindarios, x, y, DueDate, customer, Demand, neib, true, numeroIteraciones, soluciones,mtxDistancia);
                    t7.start();
                }
                if (i == 7) {
                    t8 = new hilo(opInicial, opFinal, numeroOperaciones, lecturer, directionvrpfile, vecindarios, x, y, DueDate, customer, Demand, neib, true, numeroIteraciones, soluciones,mtxDistancia);
                    t8.start();
                }

            }

        }

        do {
            try {
                Thread.sleep(0);
            } catch (InterruptedException exc) {
                System.out.println("Hilo principal interrumpido.");
            }
        } while (t1.isAlive()
                || t2.isAlive()
                || t3.isAlive()
                || t4.isAlive()
                || t5.isAlive()
                || t6.isAlive()
                || t7.isAlive()
                || t8.isAlive());

        solution local = hilo.getBestSolLocal();

        return soluciones;
    }

    LinkedList<Integer> x = new LinkedList<>();
    LinkedList<Integer> y = new LinkedList<>();
    LinkedList<Integer> DueDate = new LinkedList<>();
    LinkedList<Integer> customer = new LinkedList<>();
    LinkedList<Integer> Demand = new LinkedList<>();
    int neib;
    int numeroIteraciones;

    public void readTSP(int neib, String file) throws FileNotFoundException, IOException {
        this.neib = neib;
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String bfRead;
        int contador = 0;
        while ((bfRead = bf.readLine()) != null) {
            StringTokenizer lineasConEspacios = new StringTokenizer(bfRead);
            String cadenaLetras[] = bfRead.split(" ");
            if (contador == 4) {
                //System.out.println(cadenaLetras);
            }
            if (contador >= 9 & contador<9+5*numeroPedidos+1) {
                //System.out.println(cadenaLetras);
                int contador2 = 0;
                for (int i = 0; i < cadenaLetras.length; i++) {
                    String cadenaLetra = cadenaLetras[i];
                    if (!"".equals(cadenaLetra)) {
                        switch (contador2) {
                            case 0:
                                customer.add(Integer.parseInt(cadenaLetra));
                                break;
                            case 1:
                                x.add(Integer.parseInt(cadenaLetra));
                                break;
                            case 2:
                                y.add(Integer.parseInt(cadenaLetra));
                                break;
                            case 3:
                                Demand.add(Integer.parseInt(cadenaLetra));
                                break;
                            case 4:
                                // ReadyTime.add(Integer.parseInt(cadenaLetra));
                                break;
                            case 5:
                                DueDate.add(Integer.parseInt(cadenaLetra));
                                break;
                            default:
                                break;
                        }
                        contador2++;
                    }
                }
            }
            contador++;
        }
    }
    
    public int calcularDist(int x1, int x2, int y1, int y2) {
        int x = (x1 - x2);
        int y = (y1 - y2);
        double dii = Math.pow(x, 2) + Math.pow(y, 2);
        double b = Math.sqrt(dii);
        double c = (int) Math.round(b) * 60 / 17;
        return (int) c;
    }
    int mtxDistancia[][];
    public void mtxDistance() {
        mtxDistancia = new int[x.size()][y.size()];
        for (int i = 0; i < x.size(); i++) {
            for (int j = i; j < mtxDistancia[i].length; j++) {
                int x1 = x.get(i);
                int x2 = x.get(j);
                int y1 = y.get(i);
                int y2 = y.get(j);
                int distancia = calcularDist(x1, x2, y1, y2);
                mtxDistancia[i][j] = distancia;
                mtxDistancia[j][i] = distancia;
            }
        }
    }
}
