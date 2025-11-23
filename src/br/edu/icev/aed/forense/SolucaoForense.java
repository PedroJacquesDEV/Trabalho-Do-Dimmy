package br.edu.icev.aed.forense;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SolucaoForense implements AnaliseForenseAvancada {

    public SolucaoForense() {
    }

    // Desafio 1
    //Optei por usar a interface Deque com ArrayDeque, a oracle considera stack legado e a interface Deque moderna
    //Optei tambem utilizar indexOf e substring, já que o trabalho exige performance
    @Override
    public Set<String> encontrarSessoesInvalidas(String arquivo) throws IOException {
        Map<String, Deque<String>> sessoesDeUsuario = new HashMap<>();
        Set<String> sessoesInvalidas = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha = br.readLine();

            while ((linha = br.readLine()) != null) {
                int c1 = linha.indexOf(','); if (c1 == -1) continue;
                int c2 = linha.indexOf(',', c1 + 1); if (c2 == -1) continue;
                int c3 = linha.indexOf(',', c2 + 1); if (c3 == -1) continue;
                int c4 = linha.indexOf(',', c3 + 1);
                int fimAction = (c4 == -1) ? linha.length() : c4;

                String userId = linha.substring(c1 + 1, c2);
                String sessionId = linha.substring(c2 + 1, c3);
                String action = linha.substring(c3 + 1, fimAction);

                Deque<String> stack = sessoesDeUsuario.computeIfAbsent(userId, k -> new ArrayDeque<>());

                if ("LOGIN".equals(action)) {
                    if (!stack.isEmpty()) sessoesInvalidas.add(sessionId);
                    stack.push(sessionId);
                } else if ("LOGOUT".equals(action)) {
                    if (stack.isEmpty() || !stack.peek().equals(sessionId)) {
                        sessoesInvalidas.add(sessionId);
                    } else {
                        stack.pop();
                    }
                }
            }
        }

        for (Deque<String> stack : sessoesDeUsuario.values()) {
            while (!stack.isEmpty()) sessoesInvalidas.add(stack.pop());
        }
        return sessoesInvalidas;
    }

    // Desafio 2
    //No pdf fala que o timestamp vem em ordem crescente, então entendi que não vou precisar implementar sort
    //Sigo utilizando Indexof e substring para ter uma performance melhor.
    @Override
    public List<String> reconstruirLinhaTempo(String arquivo, String sessionIdAlvo) throws IOException {
        Queue<String> filaAcoes = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha = br.readLine();

            while ((linha = br.readLine()) != null) {
                int c1 = linha.indexOf(','); if (c1 == -1) continue;
                int c2 = linha.indexOf(',', c1 + 1); if (c2 == -1) continue;
                int c3 = linha.indexOf(',', c2 + 1); if (c3 == -1) continue;

                if ((c3 - (c2 + 1)) == sessionIdAlvo.length()) {
                    String sIdAtual = linha.substring(c2 + 1, c3);
                    if (sIdAtual.equals(sessionIdAlvo)) {
                        int c4 = linha.indexOf(',', c3 + 1);
                        int fimAction = (c4 == -1) ? linha.length() : c4;
                        String action = linha.substring(c3 + 1, fimAction);
                        filaAcoes.add(action);
                    }
                }
            }
        }
        return new ArrayList<>(filaAcoes);
    }
    // Desafio 3
    //Optei por usar Heap, ela semi ordena os elementos automaticamente, já uma lista comum iria precisar ordenar tudo no final
    //Implementei a pq com um comparador reverso fazendo ele atuar como um max-heap e deixando a maior severidade no topo
    @Override
    public List<Alerta> priorizarAlertas(String arquivo, int n) throws IOException {
        PriorityQueue<Alerta> pq = new PriorityQueue<>((a1, a2) -> Integer.compare(a2.getSeverityLevel(), a1.getSeverityLevel()));

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha = br.readLine();

            while ((linha = br.readLine()) != null) {
                int c1 = linha.indexOf(','); if (c1 == -1) continue;
                int c2 = linha.indexOf(',', c1 + 1); if (c2 == -1) continue;
                int c3 = linha.indexOf(',', c2 + 1); if (c3 == -1) continue;
                int c4 = linha.indexOf(',', c3 + 1); if (c4 == -1) continue;
                int c5 = linha.indexOf(',', c4 + 1); if (c5 == -1) continue;
                int c6 = linha.indexOf(',', c5 + 1);

                long timestamp = Long.parseLong(linha.substring(0, c1));
                String userId = linha.substring(c1 + 1, c2);
                String sessionId = linha.substring(c2 + 1, c3);
                String action = linha.substring(c3 + 1, c4);
                String resource = linha.substring(c4 + 1, c5);
                int severity = Integer.parseInt(linha.substring(c5 + 1, (c6 == -1 ? linha.length() : c6)));

                long bytes = 0;
                if (c6 != -1 && c6 < linha.length() - 1) {
                    try {
                        String bytesStr = linha.substring(c6 + 1).trim();
                        if (!bytesStr.isEmpty()) {
                            bytes = Long.parseLong(bytesStr);
                        }
                    } catch (Exception e) { bytes = 0; }
                }

                pq.add(new Alerta(timestamp, userId, sessionId, action, resource, severity, bytes));
            }
        }

        List<Alerta> resultado = new ArrayList<>();
        for (int i = 0; i < n && !pq.isEmpty(); i++) {
            resultado.add(pq.poll());
        }
        return resultado;
    }
    // Desafio 4
    //Implementei a Pilha Monotônica padrão
    //Sigo utilizando Indexof e substring para ter uma performance melhor.
    @Override
    public Map<Long, Long> encontrarPicosTransferencia(String arquivo) throws IOException {
        List<long[]> eventos = new ArrayList<>(10000);

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha = br.readLine();

            while ((linha = br.readLine()) != null) {
                int c1 = linha.indexOf(',');
                int c6 = -1;
                int count = 0;

                for (int i = 0; i < linha.length(); i++) {
                    if (linha.charAt(i) == ',') {
                        count++;
                        if (count == 6) {
                            c6 = i;
                            break;
                        }
                    }
                }

                if (c1 != -1 && c6 != -1) {
                    try {
                        int nextC = linha.indexOf(',', c6 + 1);
                        int end = (nextC == -1) ? linha.length() : nextC;
                        String bStr = linha.substring(c6 + 1, end).trim();

                        if (!bStr.isEmpty()) {
                            long bytes = Long.parseLong(bStr);
                            if (bytes > 0) {
                                long ts = Long.parseLong(linha.substring(0, c1));
                                eventos.add(new long[]{ts, bytes});
                            }
                        }
                    } catch (Exception e) {}
                }
            }
        }

        Stack<Integer> stack = new Stack<>();
        Map<Long, Long> resultado = new HashMap<>();

        for (int i = eventos.size() - 1; i >= 0; i--) {
            long[] atual = eventos.get(i);
            while (!stack.isEmpty()) {
                long[] topo = eventos.get(stack.peek());
                if (topo[1] <= atual[1]) {
                    stack.pop();
                } else {
                    break;
                }
            }
            if (!stack.isEmpty()) {
                resultado.put(atual[0], eventos.get(stack.peek())[0]);
            }
            stack.push(i);
        }
        return resultado;
    }

    @Override
    public Optional<List<String>> rastrearContaminacao(String arquivo, String org, String dest) throws IOException {
        return Optional.empty(); // TODO: Implementar
    }
}