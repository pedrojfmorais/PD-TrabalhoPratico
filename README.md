# PD-TrabalhoPratico

Pasta IMPORTANTE
  Import par ao Postman -> Meta2-PD.postman_collection.json 
  
  Run configurations -> Parâmetros para meter a correr.txt
    Nome: Server 9001
      VM option: -Djava.rmi.server.hostname=127.0.0.1
      Program arguments: 9001 PD-2022-23-TP1.db

    Nome: Server 9002
      VM option: -Djava.rmi.server.hostname=127.0.0.1
      Program arguments: 9002 PD-2022-23-TP.db

    Nome: Client
      Program arguments: 127.0.0.1 9002
      Allow multiple instances

    Nome: RmiClient
      VM option: -Djava.rmi.server.hostname=127.0.0.1
      Program arguments: 127.0.0.1 9002
      Allow multiple instances


 
