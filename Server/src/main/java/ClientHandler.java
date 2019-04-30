//import java.io.*;
//import java.net.Socket;
//
//public class ClientHandler {
//
//    Socket socket = null;
//    Server server;
//    DataInputStream in;
//    DataOutputStream out;
//
//
//    private String nick;
//
//    public ClientHandler(Server server, Socket socket) {
//        try {
//            this.server = server;
//            this.socket = socket;
//            this.in = new DataInputStream(socket.getInputStream());
//            this.out = new DataOutputStream(socket.getOutputStream());
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        nick = in.readUTF();
//                        while (true) {
//                            byte i = in.readByte();
//                            if (i == 97){
//                                Command command = Command.receiveCommand(in);
//                                System.out.println("команда получена");
//                                System.out.println(command.getId());
//                                System.out.println(command.getMsg());
//                                if (command.getId() == CommandTable.SEND_FILE){
//                                    sendResponseTo(true, "ok", command.getFileName()); //здесь еще должна быть проверка возможности передачи файла
//                                    FileBox file = FileBox.receiveFile(in);
//
//                                }
//                                if (command.getId() == CommandTable.UPLOAD_FILE){
//                                    System.out.println("Клиент хочет отправить файл");
//                                    //добавить проверку возможности загрузки файла (есть ли место)
//                                    sendResponseTo(true, "ok", command.getMsg());
//                                    System.out.println("ответ отправлен");
//                                    FileBox file = FileBox.receiveFile(in);
//                                    System.out.println("File is received");
//                                    FileOutputStream fos = new FileOutputStream("new" + file.getFileName());
//                                    for (byte b:file.getContent()) {
//                                        fos.write(b);
//                                    }
//                                    System.out.println("file is uploaded");
//                                    sendResponseTo("Файл " + file.getFileName() + " загружен успешно");
//                                }
//                                if (command.getId() == CommandTable.SEND_MESSAGE){
//
//                                }
//                            }
//
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        try {
//                            in.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            out.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            socket.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendResponseTo(boolean permission, String msg, String file) {
//        Command response = new Command(CommandTable.SERVER_RESPONSE, permission, msg, file);
//        try {
//            ObjectOutputStream oos = new ObjectOutputStream(out);
//            oos.writeObject(response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendResponseTo(String msg) {
//        Command response = new Command(CommandTable.SERVER_RESPONSE, msg);
//        try {
//            ObjectOutputStream oos = new ObjectOutputStream(out);
//            oos.writeObject(response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void sendMsg(String msg) {
//        try {
//            out.writeUTF(msg);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public String getNick() {
//        return this.nick;
//    }
//
//}
