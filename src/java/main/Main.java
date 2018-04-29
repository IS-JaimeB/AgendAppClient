package main;
import java.io.IOException;
import userclient.UserClient;
import java.util.Scanner;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import noteclient.NoteClient;

public class Main {
    static UserClient userClient = new UserClient();
    static NoteClient noteClient = new NoteClient();
    static String email = "";
    static String clave = "";
    static String nNotes = "";
    static String id = "";
    static String check = "";
    static boolean running = true;
    
    public static void main(String[] args) throws IOException {
        
        System.out.println("Welcome to AgendApp");
        System.out.println("1 - Login");
        System.out.println("2 - Create account");
        
        while (running) {
            switch (new Scanner(System.in).nextInt()) {
                case 1:
                    login();
                break;
                
                case 2:
                    createAccount();
                 break;
                 
                default:
                    System.out.println("\nIncorrect option");
                break;
            }
            
        }
    }
    
    
    private static void getEmailClave() throws IOException {
        System.out.print("Enter email: ");
        email = new Scanner(System.in).nextLine();
        System.out.print("Enter password: ");
        clave = new Scanner(System.in).nextLine(); 
    }
        
    
    private static void login() throws IOException{
        System.out.println("\n--- Sign in ---");
        getEmailClave();
        
        Response r1 = userClient.login(Response.class, email, clave);
            
        if (r1.getStatus() == 200) {
            getNotesMenu();
            running = false;
            System.out.println("\nSession has been closed");
        } else {
            running = true;
            System.out.println("\nIncorrect data\n");
            login();
        }          
    }
    
    private static void createAccount() throws IOException {
        System.out.println("\n--- Sign up ---");
        getEmailClave();
        JsonObject newUser = Json.createObjectBuilder().add("email", email).add("password", clave).build();
        Response r2 = userClient.find_JSON(Response.class, email);
                   
        if(r2.getStatus() == 204){
            userClient.registerUser(newUser, email, clave);
            System.out.println("\nAccount has been created");
            login();
        } else {
                System.out.println("\nAccount has not been created");
                createAccount();
        }         
    }
    
    

    private static void getNotesMenu() {
        
        while(running) {
            System.out.println("\n--------------------");
            System.out.println("1 - My notes");
            System.out.println("2 - Create a note");
            System.out.println("3 - Search a note");
            System.out.println("4 - Modify a note");
            System.out.println("5 - Delete a note");
            System.out.println("6 - Modify password");
            System.out.println("7 - Logout");
            System.out.println("8 - Delete account");     
            
            switch (new Scanner(System.in).nextInt()) {
                case 1 : 
                    myNotes();  
                    break;
                case 2:
                    addNote();
                    break;
                case 3:
                    searchNote();
                    break;
                case 4:
                    modifyNote();
                    break;
                case 5:
                    deleteNote();
                    break;
                case 6:
                    modifyPassword();
                    break;
                case 7: 
                    running = false;
                    break;
                case 8:
                    running = deleteAccount();
                    break;
                default:
                    System.out.println("\nIncorrect option");
                    break;
            }
        }
    }
    
    private static void myNotes(){
        String notes = noteClient.viewNotes(email);
        nNotes = noteClient.countNotes(email);  
        if (nNotes.equals("0")) {
            System.out.println("\nYou don´t have any notes");
        } else {
            System.out.println("\nMy notes");
            System.out.println(notes);                  
        }         
    }
    
    private static void addNote(){
        System.out.println("\n--- Create a note ---");
        id = (Integer.parseInt(noteClient.countREST()) + 1) + "";
        System.out.print("\nEnter content of the note: ");
        String content = new Scanner(System.in).nextLine();
        JsonObject newNote = Json.createObjectBuilder().add("id", id).add("email", email).add("content", content).build();
        noteClient.addNote(newNote, id, email, content);
        Response r3 = noteClient.find_JSON(Response.class, id);
        if(r3.getStatus() == 200){
            System.out.println("\nNote has been created.");
        } else {
            System.out.println("\nNote has not been created.");           
        }
    }
    
    
    private static void searchNote(){
        System.out.println("\n--- Seach a note ---");
        nNotes = noteClient.countNotes(email);
        if (nNotes.equals("0")) {
            System.out.println("\nYou don´t have any notes");
        } else {
            System.out.print("\nEnter ID of the note: ");
            id = new Scanner(System.in).nextLine();
            check = noteClient.checkNoteOwner(id, email);
            if (check.equals("Ok")) {
                System.out.println("\nNote:");
                System.out.println(noteClient.searchNote(id));
            } else {
                System.out.println("\nEnter a valid ID");
            }  
        }         
    }
    
    private static void modifyNote(){
        System.out.println("\n--- Modify a note ---");
        nNotes = noteClient.countNotes(email);
        if (nNotes.equals("0")) {
            System.out.println("\nYou don´t have any notes to modify");
        } else {
            System.out.print("\nEnter ID of the note to be modified: ");
            id = new Scanner(System.in).nextLine();
            check = noteClient.checkNoteOwner(id, email);
            if (check.equals("Ok")) {
                System.out.print("\nEnter a content:");
                String modified = noteClient.modifyNote(id, new Scanner(System.in).nextLine());
                if (modified.equals("Ok")) {
                    System.out.println("\nThe note has been modified");
                } else {
                    System.out.println("\nThe note hasn´t been modified");
                }   
            } else {
                System.out.println("\nEnter a valid ID");
            }  
        }        
    }
    
    private static void deleteNote(){
        System.out.println("\n--- Delete a note ---");
        nNotes = noteClient.countNotes(email);
        if (nNotes.equals("0")) {
            System.out.println("\nYou don´t have any notes to delete");
        } else {
            System.out.print("\nEnter ID of the note to be deleted: ");
            id = new Scanner(System.in).nextLine();
            check = noteClient.checkNoteOwner(id, email);
            if (check.equals("Ok")) {
                String deleted = noteClient.deleteNote(id);
                if (deleted.equals("Ok")) {
                    System.out.println("\nThe note has been deleted");
                } else {
                    System.out.println("\nThe note hasn´t been deleted");
                }   
            } else {
                System.out.println("\nEnter a valid ID");
            }
        }            
    }
    
    
    private static void modifyPassword(){
        System.out.println("\n--- Modify password ---"); 
        System.out.print("\nEnter a new password: ");
        check = userClient.modifyPassword(email, new Scanner(System.in).nextLine());
        if (check.equals("Ok")) {
            System.out.println("\nPassword has been modified");
        } else {
            System.out.println("\nPassword hasn´t been modified");
        }        
    }
    
    private static boolean deleteAccount(){
        System.out.println("\n--- Delete account ---"); 
        noteClient.deleteAllNotesFromUser(Response.class, email);
        Response r1 = userClient.deleteAccount(Response.class, email);
        if (r1.getStatus() == 204) {
            System.out.println("\nYour account has been deleted");
            return false;
        } else {
            System.out.println("\nYour account hasn´t been deleted");
        }    
        return true;
    }
}