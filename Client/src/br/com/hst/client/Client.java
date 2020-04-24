package br.com.hst.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author internship_team
 * Client Side 
 * It's here what you inform your name and server port.
 */
public class Client {

	private static Scanner sc;

	public static void main(String[] args) {
		try {
			sc = new Scanner(System.in);
			System.out.println("Digite a porta do servidor: ");
			int port = sc.nextInt();
			System.out.println("Digite o seu nome: ");
			String name = sc.next();

			Socket client = new Socket(InetAddress.getLocalHost().getHostAddress(), port);
			RequestHandler requestHandler = new RequestHandler(client);
			DataInputStream dis = new DataInputStream(client.getInputStream());
			DataOutputStream dos = new DataOutputStream(client.getOutputStream());
			dos.writeUTF(name);
			requestHandler.startReceiveThread(dis, dos);
			requestHandler.startInputThread(dis, dos);
		} catch (UnknownHostException e) {
			System.out.println("Erro desconhecido");
		} catch (IOException e) {
			System.out.println("Falha ao conectar ao servidor");
		}
	}
}