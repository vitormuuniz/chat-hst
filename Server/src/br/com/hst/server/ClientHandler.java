package br.com.hst.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;

import br.com.hst.server.constants.MenuConstants;

public class ClientHandler implements Runnable {
	private Map<String, ClientHandler> clientList;
	private Socket client;
	private String name;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Scanner sc;

	public ClientHandler(Socket client, Map<String, ClientHandler> clientList, String name, DataInputStream dis,
			DataOutputStream dos) {
		this.client = client;
		this.clientList = clientList;
		this.name = name;
		this.dis = dis;
		this.dos = dos;
	}

	@Override
	public void run() {
		try {
			int opcao = 999;
			while (opcao != 4) {
				dos.writeUTF(MenuConstants.MENU);
				opcao = dis.readInt();
				switch (opcao) {
				case 1:
					listUser();
					break;
				case 2:
					String targetMessage = dis.readUTF();
					sendMessage(targetMessage);
					break;
				case 3:
					String target = dis.readUTF();
					sendFile(target);
					break;
				case 4:
					System.out.println("O cliente " + this.name + " fechou a conex�o.");
					client.close();
					clientList.remove(this.name);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void listUser() throws IOException {
		dos.writeUTF(MenuConstants.LIST_USERS);
		clientList = Server.getClientList();
		dos.writeInt(clientList.size());
		clientList.forEach((key, value) -> {
			try {
				dos.writeUTF(key);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void sendFile(String target) {
		try {
			DataOutputStream targetClient = Server.getClientList().get(target).getDataOutputStream();
			targetClient.writeUTF(MenuConstants.SEND_FILES);

			String filename = dis.readUTF();
			int fileSize = dis.readInt();
			int bufferSize = fileSize < 4096 ? fileSize : 4096;

			targetClient.writeUTF("received_" + System.currentTimeMillis() + "_" + filename);
			targetClient.writeInt(fileSize);

			byte[] buffer = new byte[bufferSize];
			int remaining = fileSize;
			while (remaining > 0) {
				dis.read(buffer);
				if (remaining < bufferSize) {
					byte[] bufferOld = buffer;
					buffer = new byte[remaining];
					System.arraycopy(bufferOld, 0, buffer, 0, buffer.length);
				}
				targetClient.write(buffer);
				remaining -= buffer.length;
			}
			System.out.println("\nArquivo " + filename + " recebido e redirecionado para " + target);

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Falha ao ler request");
		}
	}

	public DataOutputStream getDataOutputStream() {
		return dos;
	}

	protected void sendMessage(String targetMessage) {
		try {
			DataOutputStream targetClient = Server.getClientList().get(targetMessage).getDataOutputStream();
			targetClient.writeUTF(MenuConstants.SEND_MESSAGE);
			String message = dis.readUTF();

			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			String hora = dateFormat.format(new Date());
			targetClient.writeUTF(name + " [" + hora + "]: " + message);

			targetClient.writeUTF(MenuConstants.MENU);
		} catch (IOException e) {
			System.out.println("Falha ao enviar mensagem");
		}
	}
}