package br.com.hst.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author internship_team Client Side It's here what you inform your name and
 *         server port.
 */
public class Client {

	private static Scanner sc;
	private static List<String> listUsers = new ArrayList<String>();

	public static void main(String[] args) {
		try {
			sc = new Scanner(System.in);
			System.out.println("Digite a porta do servidor: ");
			int port = sc.nextInt();
			Socket client = new Socket("localhost", port);
			RequestHandler requestHandler = new RequestHandler(client);
			DataInputStream dis = new DataInputStream(client.getInputStream());
			DataOutputStream dos = new DataOutputStream(client.getOutputStream());
			System.out.println("Digite o seu nome: ");
			String name = sc.next();
			loadListUsers(dis);
			while(listUsers.contains(name)) {
				System.out.println("Usuario ja existente.\nDigite o seu nome: ");
				name = sc.next();
			}
			dos.writeUTF(name);
			requestHandler.startReceiveThread(dis, dos);
			requestHandler.startInputThread(dis, dos, name);
		} catch (UnknownHostException e) {
			System.out.println("Erro desconhecido");
		} catch (IOException e) {
			System.out.println("Falha ao conectar ao servidor");
		}
	}

	private static void loadListUsers(DataInputStream dis) {
		int size;
		try {
			size = dis.readInt();
			for (int i = 0; i < size; i++) {
				String names = dis.readUTF();
				listUsers.add(names);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}