/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author rianveal
 */
public class Conexion {
  private Connection con = null;
  private boolean connectToDB = false;
  private final String connectionDB = "java:app/ServicioConexion"; // java:app/ServicioConexion | jdbc/RecursoApi
  private String codigoReferenciaServicio = "ARHS";

  
  public void connect(){
    try {  
      Class.forName("com.mysql.jdbc.Driver");
      con=DriverManager.getConnection("jdbc:mysql://localhost:3306/DBSTORE","store","store2020_");
      System.out.println(">> "+codigoReferenciaServicio+" - Conexión exitosa!"); 
      connectToDB = true;
    } catch (ClassNotFoundException ex) {
      System.out.println("Conexión no establecida - clase no econttrda. Excepción es : "+ex);
      connectToDB = false;
    } catch (SQLException ex) {
      System.out.println("Conexión no establecida - Excepción SQL, Excepción es: - "+ex);
      connectToDB = false;
    }
      
  }
  
  public void connectar(){ 
    try {
      Context ctx = new InitialContext();
      DataSource ds = (DataSource) ctx.lookup(connectionDB);    
      con = ds.getConnection();
      System.out.println(">> "+codigoReferenciaServicio+" - Conexión exitosa..!"); 
      connectToDB = true;
    }catch (NamingException ex) {
      System.out.println(">> "+codigoReferenciaServicio+" - Conexión no establecida - nombre del recurso no encontrado."
              + "\nException is : "+ex);
      connectToDB = false;
    }catch (SQLException ex) {
      System.out.println(">> "+codigoReferenciaServicio+" - Conexión no establecida..!"
              + "\nException is : "+ex);
      connectToDB = false;
    }  
  }

  public void disconnect(){
    try {
      con.close();
      connectToDB = false;
      System.out.println(">> "+codigoReferenciaServicio+" - Desconectado de la BD.");
    } catch (SQLException e) {
      System.out.println(">> "+codigoReferenciaServicio+" - Una excepción ocurrió mientras se desconectaba de la BD."
              + "\nException is : "+e);
    }
  }

  public Connection getCon() {
    return con;
  }

  public void setCon(Connection con) {
    this.con = con;
  }

  public boolean validateConnectionDB(){
    boolean reply = false;
    this.connect();
    if( connectToDB ){
      reply = true;
      this.disconnect();
    }else{
      reply = false;
    }
    return reply;
  }
}
