package aMarcoTest;

import server.model.data.viewModels.Lugar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class testing {

    public static String prepareToSave(String token) {
        do {
            // Removes spaces at the beginning or ending
            token = token.trim();

            // Removes quotes
            token = token.replaceAll("\"", "");

        } while(token.startsWith("\"") || token.endsWith("\"") || token.startsWith(" ") || token.endsWith(" "));
        return token;
    }

    /*
     * Lugares de erro no exemplo:
     *  Tipo - Tem um : a separar, em vez de ;
     *  Repete a fila E e F
     */
    public static boolean inserirEspetaculo(String filename) throws ParseException {

        int id = -1;
        boolean visibilidade = false;
        String designacao = "";
        String tipo = "";
        Date data = new Date();
        int duracao = 0;
        String local = "";
        String localidade = "";
        String pais = "";
        String classificacao = "";
        List<Lugar> lugares = new ArrayList<>();

        String dias = "", horas = "";

        filename = "src/aMarcoTest/espetaculo.csv";

        try(FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr)){

            String line;
            String linePurpose;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(";");
                linePurpose = prepareToSave(tokens[0]);

                if(linePurpose.equals("Designação")) {
                    designacao = prepareToSave(tokens[1]);
                } else if (linePurpose.equals("Tipo")) {
                    tipo = prepareToSave(tokens[1]);
                } else if (linePurpose.equals("Data")) {
                    dias = prepareToSave(tokens[1]) + "/" +
                            prepareToSave(tokens[2]) + "/" +
                            prepareToSave(tokens[3]);
                    /*data = new SimpleDateFormat("dd/MM/yyyy").parse(dias);*/
                } else if (linePurpose.equals("Hora")) {
                    horas = prepareToSave(tokens[1]) + ":" + prepareToSave(tokens[2]);
                    //Date hora = new SimpleDateFormat("HH:mm").parse(horas);
                    //System.out.println(hora);
                    //data.setTime(hora.getTime() + data.getTime());
                } else if (linePurpose.equals("Duração")) {
                    duracao = Integer.parseInt(prepareToSave(tokens[1]));
                } else if (linePurpose.equals("Local")) {
                    local = prepareToSave(tokens[1]);
                } else if (linePurpose.equals("Localidade")) {
                    localidade = prepareToSave(tokens[1]);
                } else if (linePurpose.equals("País")) {
                    pais = prepareToSave(tokens[1]);
                } else if (linePurpose.equals("Classificação etária")) {
                    classificacao = prepareToSave(tokens[1]);
                } else if (linePurpose.equals("Fila")) {
                } else if(linePurpose.length() == 1) {
                    //TODO Filas
                    String[] modifiedTokens = Arrays.copyOfRange(tokens, 1, tokens.length);

                    for (String tok : modifiedTokens) {
                        String[] seat = prepareToSave(tok).split(":");
                        // ID??????
                        Lugar lugar = new Lugar(-1, linePurpose.toUpperCase(), seat[0], Double.parseDouble(seat[1]));
                        if(lugares.contains(lugar)) {
                            // TODO CANCEL EVERYTHING
                            System.out.println("Errou " + linePurpose.toUpperCase() + " " + seat[0] + " " + seat[1]);
                        }
                        lugares.add(lugar);
                    }
                } else {
                    // TODO CANCEL EVERYTHING
                    if(linePurpose.length() != 0)
                        System.out.println("MORREU " + line + " " + linePurpose);
                }

                /*System.out.println(data);
                System.out.println(designacao);
                System.out.println(tipo);
                System.out.println(duracao);
                System.out.println(local);
                System.out.println(localidade);
                System.out.println(pais);
                System.out.println(classificacao);
                System.out.println(lugares);
                System.out.println();
                System.out.println();*/

                //System.out.println(line);


                // for(String tok : tokens) {
                //     tok = prepareToSave(tok);
                    // tok = tok.trim();
                    // if(tok.startsWith("\"")) {
                    //     System.out.println("yes");
                    // }
                    // /*else if(tok.startsWith(" "))
                    //     System.out.println("yes2");*/
                    // if(tok.endsWith("\""))
                    //     System.out.println("no");
                    // /*else if(tok.endsWith(" "))
                    //     System.out.println("no2");*/
                    // System.out.println(tok);
                // }


            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        data = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dias + " " + horas);
        System.out.println(data);




        /*try {

            data.getTcpConnection().sendMsg(
                    new MsgTcp(
                            TypeMsgTCP.CLIENT,
                            MessagesTCPOperation.ESPE,
                            new Espetaculo(id, visibilidade, designacao, tipo, data, duracao, local, localidade, pais, classificacao, lugares)
                    )
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }*/


        return true;
    }

    public static void main(String[] args) throws ParseException {

        inserirEspetaculo("");

    }

}
