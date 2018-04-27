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
                    System.out.println("--- Sign up ---");
                    getEmailClave();
                    JsonObject newUser = Json.createObjectBuilder().add("email", email).add("password", clave).build();
                    Response r2 = userClient.find_JSON(Response.class, email);
                   
                    if(r2.getStatus() == 204){
                        userClient.registerUser(newUser, email, clave);
                        System.out.println("Account has been created");
                        login();
                    } else {
                        System.out.println("Account has not been created");
                    }                 
                    
                 break;
                 
                default:
                    System.out.println("Incorrect option");
                break;
            }
            
        }
    }
    
    
    private static void login() throws IOException{
        System.out.println("--- Sign in ---");
        getEmailClave();
        
        Response r1 = userClient.login(Response.class, email, clave);
            
        if (r1.getStatus() == 200) {
            getNotesMenu();
            running = false;
            System.out.println("\nSession has been closed");
        } else {
            running = true;
            System.out.println("\nIncorrect data\n");
        }          
    }
    
    private static void getEmailClave() throws IOException {
        System.out.print("Enter email: ");
        email = new Scanner(System.in).nextLine();
        System.out.print("Enter password: ");
        clave = new Scanner(System.in).nextLine(); 
    }
    
    private static void getNotesMenu() {
        
        boolean running = true;
        
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
            System.out.println("--------------------");       
            
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
                    deleteAccount();
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
        System.out.println("--- Create a note ---");
        String newId = generarId();
        System.out.println("Introduzca el contenido de la nota");
        String content = new Scanner(System.in).nextLine();
        JsonObject newNote = Json.createObjectBuilder().add("id", newId).add("email", email).add("content", content).build();
        noteClient.addNote(newNote, newId, email, content);
        Response r3 = noteClient.find_JSON(Response.class, newId);
        if(r3.getStatus() == 200){
            System.out.println("\nNote has been created.");
        } else {
            System.out.println("\nNote has not been created.");           
        }
    }
    
    
    private static void searchNote(){
        System.out.println("--- Seach a note ---");
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
        System.out.println("--- Modify a note ---");
        nNotes = noteClient.countNotes(email);
        if (nNotes.equals("0")) {
            System.out.println("\nYou don´t have any notes to modify");
        } else {
            System.out.print("\nEnter ID of the note to be modified: ");
            id = new Scanner(System.in).nextLine();
            check = noteClient.checkNoteOwner(id, email);
            if (check.equals("Ok")) {
                System.out.print("Enter a content:");
                String modified = noteClient.modifyNote(id, new Scanner(System.in).nextLine());
                if (modified.equals("Ok")) {
                    System.out.println("The note has been modified");
                } else {
                    System.out.println("The note hasn´t been modified");
                }   
            } else {
                System.out.println("\nEnter a valid ID");
            }  
        }        
    }
    
    private static void deleteNote(){
        System.out.println("--- Delete a note ---");
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
        System.out.println("--- Modify password ---"); 
        System.out.print("Enter a new password: ");
        check = userClient.modifyPassword(email, new Scanner(System.in).nextLine());
        if (check.equals("Ok")) {
            System.out.println("Password has been modified");
        } else {
            System.out.println("Password hasn´t been modified");
        }        
    }
    
    private static void deleteAccount(){
        System.out.println("--- Delete account ---");     
        Response r1 = userClient.deleteAccount(Response.class, email);
        if (r1.getStatus() == 204) {
            running = false;
            System.out.println("Your account has been deleted");
        } else {
            System.out.println("Your account hasn´t been deleted");
        }             
    }
    
    
    private static String generarId(){
        int num1 = 1;
        int num2 = 1000;
        String newId = (int)Math.floor(Math.random()*(num1-(num2+1))+(num2)) + "";
        Response r4 = noteClient.existsNote(Response.class, newId);

        while (r4.getStatus() == 200) {
            r4 = noteClient.existsNote(Response.class, newId);
            newId = (int)Math.floor(Math.random()*(num1-(num2+1))+(num2)) + "";
        }
        return newId;
    }
    
}