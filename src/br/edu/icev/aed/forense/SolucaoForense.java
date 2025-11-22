package br.edu.icev.aed.forense;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SolucaoForense implements AnaliseForenseAvancada {

    public SolucaoForense() {
    }

    @Override
    public Set<String> encontrarSessoesInvalidas(String arquivo) throws IOException {
        return new HashSet<>(); // TODO: Implementar
    }

    @Override
    public List<String> reconstruirLinhaTempo(String arquivo, String sessionId) throws IOException {
        return new ArrayList<>(); // TODO: Implementar
    }

    @Override
    public List<Alerta> priorizarAlertas(String arquivo, int n) throws IOException {
        return new ArrayList<>(); // TODO: Implementar
    }

    @Override
    public Map<Long, Long> encontrarPicosTransferencia(String arquivo) throws IOException {
        return new HashMap<>(); // TODO: Implementar
    }

    @Override
    public Optional<List<String>> rastrearContaminacao(String arquivo, String org, String dest) throws IOException {
        return Optional.empty(); // TODO: Implementar
    }
}