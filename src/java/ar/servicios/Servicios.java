/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.servicios;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rianveal
 */
public class Servicios  {
    
  public String codigoReferenciaServicio = "ARHS";
    
  public String validateConnectionDB(){
    String reply = "NO";
    Conexion connection = new Conexion();
    if( connection.validateConnectionDB() ){
      reply = "YES";
    }
    return reply;
  }

  public String validate_credentials(String user, String password){
    Conexion connection = new Conexion();
    Statement st = null;
    ResultSet rs = null;
    Gson gson = new Gson();
    String[] dataNO = {"NO"};
    String reply = gson.toJson(dataNO);
    String sentence = "SELECT USUARIO, ID_PERFIL, (SELECT NOMBRE FROM PERFILES WHERE ID = ID_PERFIL) NOM_PERFIL,\n" +
                      "ESTADO, \n" +
                      "PRS_DOCUMENTO DOCUMENTO, (SELECT PRIMER_NOMBRE||' '||PRIMER_APELLIDO FROM PERSONAS WHERE DOCUMENTO = PRS_DOCUMENTO) PERSONA \n" +
                      "FROM USUARIOS \n" +  
                      "WHERE USUARIO = '"+user+"' AND CONTRASENA = '"+password+"'";
    if( validateConnectionDB().equals("YES") ){
      connection.connect();
      try {
        st = connection.getCon().createStatement();
        rs = st.executeQuery(sentence);
        if (rs.next()) {
          JsonObject dataPerson = new JsonObject();
          dataPerson.addProperty("userId", rs.getString(1) );
          dataPerson.addProperty("profileId", rs.getInt(2) );
          dataPerson.addProperty("profile", rs.getString(3) );
          dataPerson.addProperty("state", rs.getString(4) );
          dataPerson.addProperty("personId", rs.getInt(5) );
          dataPerson.addProperty("name", rs.getString(6) );
          reply = gson.toJson(dataPerson);
        }
      } catch (SQLException e) {
        System.out.println(""+codigoReferenciaServicio+" - UNA EXCEPECIÓN OCURRIO AL VALIDAR CREDENCIALES  :"+e);
        connection.disconnect();
      } 
      connection.disconnect();
    }else{
      String[] dataNCDB = {"NCDB"};
      reply = gson.toJson(dataNCDB);
    }    
    return reply;
  }
  
  public String get_all_products()  {
    Conexion connection = new Conexion();
    Statement st = null;
    ResultSet rs = null;
    Gson gson = new Gson();
    String [] objects = null;
    String[] dataNO = {"NO"};
    String reply = gson.toJson(dataNO);
    List<JsonObject> list = new LinkedList<>();
    String sentence = "SELECT ID, NOMBRE, VALOR_UNITARIO, VALOR_VENTA,\n" +
                      "SALDO, TOPE_MINIMO, TOPE_MAXIMO, \n" +
                      "ID_MARCA, (SELECT NOMBRE FROM MARCAS WHERE ID = ID_MARCA) MARCA,\n" +
                      "ID_CATEGORIA, (SELECT NOMBRE FROM CATEGORIAS WHERE ID = ID_CATEGORIA) CATEGORIA,\n" +
                      "ID_PROVEEDOR, (SELECT RAZON_SOCIAL FROM PROVEEDORES WHERE ID = ID_PROVEEDOR) PROVEEDOR,\n" +
                      "ESTADO, FECHA_REGISTRO, USUARIO_REGISTRA \n" +
                      "FROM PRODUCTOS \n" +
                      "ORDER BY 2";
    if( validateConnectionDB().equals("YES") ){
      connection.connect();
      try {
        st = connection.getCon().createStatement();
        rs = st.executeQuery(sentence);
        while (rs.next()) {
          JsonObject dataProduct = new JsonObject();
          dataProduct.addProperty("id", rs.getInt(1));
          dataProduct.addProperty("name", rs.getString(2));
          dataProduct.addProperty("unitValue", desencryptProccess(rs.getString(3)));
          dataProduct.addProperty("saleValue", desencryptProccess(rs.getString(4)));
          dataProduct.addProperty("balance", rs.getInt(5));
          dataProduct.addProperty("minimumExistence", rs.getInt(6));
          dataProduct.addProperty("maximumExistence", rs.getInt(7));
          dataProduct.addProperty("brandId", rs.getInt(8));
          dataProduct.addProperty("brand", rs.getString(9));
          dataProduct.addProperty("categoryId", rs.getInt(10));
          dataProduct.addProperty("category", rs.getString(11));
          dataProduct.addProperty("providerId", rs.getInt(12));
          dataProduct.addProperty("provider", rs.getString(13));
          dataProduct.addProperty("state", rs.getString(14));
          dataProduct.addProperty("dateRegister", String.valueOf(rs.getDate(15)));
          dataProduct.addProperty("userRegister", rs.getString(16));
          list.add(dataProduct);
        }
        reply = gson.toJson(list);
      } catch (SQLException e) {
        System.out.println(""+codigoReferenciaServicio+" - UNA EXCEPCIÓN OCURRIÓ AL LISTAR LOS PRODUCTOS :"+e);
        connection.disconnect();
      } 
      connection.disconnect();
    }else{
      String[] dataNCDB = {"NCDB"};
      reply = gson.toJson(dataNCDB);
    }    
    return reply;
  }
  
  public String add_product(String jsonData){
    Conexion connection = new Conexion();
    Statement st = null;
    ResultSet rs = null;
    Gson gson = new Gson();
    String[] dataNO = {"NO"};
    String reply = gson.toJson(dataNO);
    String[] replyYES = {"YES"};
    String product = "["+jsonData+"]";
    String name = "", brandId = "", categoryId = "", unitValue = "", unitSale = "", minimumExistence = "", maximumExistence = "", balance = "", user = "";
    JsonParser parser = new JsonParser();
    JsonArray gsonArr = parser.parse(product).getAsJsonArray();
    for( JsonElement obj: gsonArr ){
      JsonObject gsonObj = obj.getAsJsonObject();
      name = gsonObj.get("name").getAsString();
      brandId = gsonObj.get("brandId").getAsString();
      categoryId = gsonObj.get("categoryId").getAsString();
      unitValue = encryptProccess(gsonObj.get("unitValue").getAsString());
      unitSale  = encryptProccess(gsonObj.get("unitSale").getAsString());
      minimumExistence = gsonObj.get("minimumExistence").getAsString();
      maximumExistence = gsonObj.get("maximumExistence").getAsString();
      balance = gsonObj.get("balance").getAsString();
      user = gsonObj.get("user").getAsString();
    }
    if( validateConnectionDB().equals("YES") ){
      connection.connect();
      try {
        String sentenceGetId = "SELECT \n" +
                               "    CASE WHEN MAX(ID) IS NULL THEN 1\n" +
                               "    ELSE MAX(ID) + 1\n" +
                               "    END ID\n" +
                               "FROM PRODUCTOS";
        st = connection.getCon().createStatement();
        rs = st.executeQuery(sentenceGetId);
        if( rs.next() ){
          int unicProductId = rs.getInt(1);
          String sentenceAddProduct = "INSERT INTO PRODUCTOS VALUES("+unicProductId+",'"+name+"','"+name+"',null,'"+unitValue+"','"+unitSale+"',"
                                  + ""+balance+","+minimumExistence+","+maximumExistence+","+brandId+","+categoryId+",0,'A',SYSDATE(),'"+user+"')";
          PreparedStatement preparedStmt = connection.getCon().prepareStatement(sentenceAddProduct);
          preparedStmt.execute();
          reply = gson.toJson(replyYES);
        }  
      } catch (SQLException e) {
        System.out.println(""+codigoReferenciaServicio+" - UNA EXCEPCIÓN OCURRIÓ AL GUARDAR EL  PRODUCTO :"+e);
        connection.disconnect();
      }
      connection.disconnect();
    }else{
      String[] dataNCDB = {"NCDB"};
      reply = gson.toJson(dataNCDB);
    } 
    return reply;
  }
  
  public String find_product(String id){
    Conexion connection = new Conexion();
    Statement st = null;
    ResultSet rs = null;
    Gson gson = new Gson();
    String[] dataNO = {"NO"};
    String reply = gson.toJson(dataNO);
    String sentence = "SELECT ID, NOMBRE, VALOR_UNITARIO, VALOR_VENTA,\n" +
                      "SALDO, TOPE_MINIMO, TOPE_MAXIMO, \n" +
                      "ID_MARCA, (SELECT NOMBRE FROM MARCAS WHERE ID = ID_MARCA) MARCA,\n" +
                      "ID_CATEGORIA, (SELECT NOMBRE FROM CATEGORIAS WHERE ID = ID_CATEGORIA) CATEGORIA,\n" +
                      "ID_PROVEEDOR, (SELECT RAZON_SOCIAL FROM PROVEEDORES WHERE ID = ID_PROVEEDOR) PROVEEDOR,\n" +
                      "ESTADO, FECHA_REGISTRO, USUARIO_REGISTRA FROM PRODUCTOS\n" +
                      "WHERE ID = "+id+"";
    if( validateConnectionDB().equals("YES") ){
      connection.connect();
      try {
        st = connection.getCon().createStatement();
        rs = st.executeQuery(sentence);
        if (rs.next()) {
          JsonObject dataProduct = new JsonObject();
          dataProduct.addProperty("id", rs.getInt(1));
          dataProduct.addProperty("name", rs.getString(2));
          dataProduct.addProperty("unitValue", desencryptProccess(rs.getString(3)));
          dataProduct.addProperty("saleValue", desencryptProccess(rs.getString(4)));
          dataProduct.addProperty("balance", rs.getInt(5));
          dataProduct.addProperty("minimumExistence", rs.getInt(6));
          dataProduct.addProperty("maximumExistence", rs.getInt(7));
          dataProduct.addProperty("brandId", rs.getInt(8));
          dataProduct.addProperty("brand", rs.getString(9));
          dataProduct.addProperty("categoryId", rs.getInt(10));
          dataProduct.addProperty("category", rs.getString(11));
          dataProduct.addProperty("providerId", rs.getInt(12));
          dataProduct.addProperty("provider", rs.getString(13));
          dataProduct.addProperty("state", rs.getString(14));
          dataProduct.addProperty("dateRegister", String.valueOf(rs.getDate(15)));
          dataProduct.addProperty("userRegister", rs.getString(16));
          
          reply = gson.toJson(dataProduct);
        }
      } catch (SQLException e) {
        System.out.println(""+codigoReferenciaServicio+" - UNA EXCEPCIÓN OCURRIÓ AL CONSULTAR EL PRODUCTO :"+e);
        connection.disconnect();
      } 
      connection.disconnect();
    }else{
      String[] dataNCDB = {"NCDB"};
      reply = gson.toJson(dataNCDB);
    }    
    return reply;
  }
  
  public String add_movement_product(String jsonData){
    Conexion connection = new Conexion();
    Statement st = null;
    ResultSet rs = null;
    PreparedStatement preparedStmt = null;
    Gson gson = new Gson();
    String[] dataNO = {"NO"};
    String reply = gson.toJson(dataNO);
    String[] replyYES = {"YES"};
    String product = "["+jsonData+"]";
    String id = "", movementType = "", quantity = "", total = "", user = "";
    JsonParser parser = new JsonParser();
    JsonArray gsonArr = parser.parse(product).getAsJsonArray();
    for( JsonElement obj: gsonArr ){
      JsonObject gsonObj = obj.getAsJsonObject();
      id = gsonObj.get("id").getAsString();
      movementType = gsonObj.get("movementType").getAsString();
      quantity = gsonObj.get("quantity").getAsString();
      total = gsonObj.get("total").getAsString();
      user = gsonObj.get("user").getAsString();
    }
    if( validateConnectionDB().equals("YES") ){
      connection.connect();
      try {
        String sentenceGetId = "SELECT \n" +
                               "    CASE WHEN MAX(ID) IS NULL THEN 1\n" +
                               "    ELSE MAX(ID) + 1\n" +
                               "    END ID\n" +
                               "FROM MOVIMIENTO_PRODUCTO";
        st = connection.getCon().createStatement();
        rs = st.executeQuery(sentenceGetId);
        if( rs.next() ){
          int unicId = rs.getInt(1);
          if( movementType.equals("s") ){
            movementType = "1";
          }else if( movementType.equals("r") ){
            movementType = "2";
          }
          String sentenceAddProduct = "INSERT INTO MOVIMIENTO_PRODUCTO VALUES("+unicId+","+movementType+","+id+","+quantity+",SYSDATE(),'"+user+"')";
          preparedStmt = connection.getCon().prepareStatement(sentenceAddProduct);
          preparedStmt.execute();
          
          String sentenceUpdateBalance = "UPDATE PRODUCTOS SET SALDO = "+total+" WHERE ID = "+id+"";
          preparedStmt = connection.getCon().prepareStatement(sentenceUpdateBalance);
          preparedStmt.execute(); 
          reply = gson.toJson(replyYES);
        }  
      } catch (SQLException e) {
        System.out.println(""+codigoReferenciaServicio+" - UNA EXCEPCIÓN OCURRIÓ AL GUARDAR EL MOVIMIENTO DEL PRODUCTO :"+e);
        connection.disconnect();
      }
      connection.disconnect();
    }else{
      String[] dataNCDB = {"NCDB"};
      reply = gson.toJson(dataNCDB);
    } 
    return reply;
  }
  
  public String update_product(String jsonData){
    Conexion connection = new Conexion();
    Statement st = null;
    ResultSet rs = null;
    Gson gson = new Gson();
    String[] dataNO = {"NO"};
    String reply = gson.toJson(dataNO);
    String[] replyYES = {"YES"};
    String product = "["+jsonData+"]";
    String id = "", name = "", brandId = "", categoryId = "", unitValue = "", unitSale = "", minimumExistence = "", maximumExistence = "",  user = "";
    JsonParser parser = new JsonParser();
    JsonArray gsonArr = parser.parse(product).getAsJsonArray();
    for( JsonElement obj: gsonArr ){
      JsonObject gsonObj = obj.getAsJsonObject();
      id = gsonObj.get("id").getAsString();
      name = gsonObj.get("name").getAsString();
      brandId = gsonObj.get("brandId").getAsString();
      categoryId = gsonObj.get("categoryId").getAsString();
      unitValue = encryptProccess(gsonObj.get("unitValue").getAsString());
      unitSale  = encryptProccess(gsonObj.get("unitSale").getAsString());
      minimumExistence = gsonObj.get("minimumExistence").getAsString();
      maximumExistence = gsonObj.get("maximumExistence").getAsString();
      user = gsonObj.get("user").getAsString();
    }
    if( validateConnectionDB().equals("YES") ){
      connection.connect();
      try {        
        String sentenceUpdateProduct = "UPDATE PRODUCTOS SET NOMBRE = '"+name+"', DESCRIPCION = '"+name+"', VALOR_UNITARIO = '"+unitValue+"', VALOR_VENTA = '"+unitSale+"', "
                                     + "TOPE_MINIMO = "+minimumExistence+", TOPE_MAXIMO = "+maximumExistence+", ID_MARCA = "+brandId+", ID_CATEGORIA = "+categoryId+", "
                                     + "FECHA_REGISTRO = SYSDATE(), USUARIO_REGISTRA = '"+user+"' WHERE ID = "+id+"";
        PreparedStatement preparedStmt = connection.getCon().prepareStatement(sentenceUpdateProduct);
        preparedStmt.execute();
        reply = gson.toJson(replyYES);
      } catch (SQLException e) {
        System.out.println(""+codigoReferenciaServicio+" - UNA EXCEPCIÓN OCURRIÓ AL ACTUALIZAR EL PRODUCTO :"+e);
        connection.disconnect();
      }
      connection.disconnect();
    }else{
      String[] dataNCDB = {"NCDB"};
      reply = gson.toJson(dataNCDB);
    } 
    return reply;
  }
  
  public String get_all_brands()  {
    Conexion connection = new Conexion();
    Statement st = null;
    ResultSet rs = null;
    Gson gson = new Gson();
    String [] objects = null;
    String[] dataNO = {"NO"};
    String reply = gson.toJson(dataNO);
    List<JsonObject> list = new LinkedList<>();
    String sentence = "SELECT ID, NOMBRE FROM MARCAS ORDER BY 2";
    if( validateConnectionDB().equals("YES") ){
      connection.connect();
      try {
        st = connection.getCon().createStatement();
        rs = st.executeQuery(sentence);
        while (rs.next()) {
          JsonObject dataBrand = new JsonObject();
          dataBrand.addProperty("id", rs.getInt(1));
          dataBrand.addProperty("value", rs.getString(2));
          list.add(dataBrand);
        }
        reply = gson.toJson(list);
      } catch (SQLException e) {
        System.out.println(""+codigoReferenciaServicio+" - UNA EXCEPCIÓN OCURRIÓ AL LISTAR LAS MARCAS :"+e);
        connection.disconnect();
      } 
      connection.disconnect();
    }else{
      String[] dataNCDB = {"NCDB"};
      reply = gson.toJson(dataNCDB);
    }    
    return reply;
  }
  
  public String add_brand(String jsonData){
    Conexion connection = new Conexion();
    Statement st = null;
    ResultSet rs = null;
    Gson gson = new Gson();
    String[] dataNO = {"NO"};
    String reply = gson.toJson(dataNO);
    String[] replyYES = {"YES"};
    String brand = "["+jsonData+"]";
    String name = "", user = "";
    JsonParser parser = new JsonParser();
    JsonArray gsonArr = parser.parse(brand).getAsJsonArray();
    for( JsonElement obj: gsonArr ){
      JsonObject gsonObj = obj.getAsJsonObject();
      name = gsonObj.get("name").getAsString();
      user = gsonObj.get("user").getAsString();
    }
    if( validateConnectionDB().equals("YES") ){
      connection.connect();
      try {
        String sentenceGetId = "SELECT \n" +
                               "    CASE WHEN MAX(ID) IS NULL THEN 1\n" +
                               "    ELSE MAX(ID) + 1\n" +
                               "    END ID\n" +
                               "FROM MARCAS";
        st = connection.getCon().createStatement();
        rs = st.executeQuery(sentenceGetId);
        if( rs.next() ){
          int unicId = rs.getInt(1);
          String sentenceAddBrand = "INSERT INTO MARCAS VALUES("+unicId+",'"+name+"','A',SYSDATE(),'"+user+"')";
          PreparedStatement preparedStmt = connection.getCon().prepareStatement(sentenceAddBrand);
          preparedStmt.execute();
          reply = gson.toJson(replyYES);
        }  
      } catch (SQLException e) {
        System.out.println(""+codigoReferenciaServicio+" - UNA EXCEPCIÓN OCURRIÓ AL GUARDAR LA MARCA :"+e);
        connection.disconnect();
      }
      connection.disconnect();
    }else{
      String[] dataNCDB = {"NCDB"};
      reply = gson.toJson(dataNCDB);
    } 
    return reply;
  }
  
  public String get_all_categories()  {
    Conexion connection = new Conexion();
    Statement st = null;
    ResultSet rs = null;
    Gson gson = new Gson();
    String [] objects = null;
    String[] dataNO = {"NO"};
    String reply = gson.toJson(dataNO);
    List<JsonObject> list = new LinkedList<>();
    String sentence = "SELECT ID, NOMBRE FROM CATEGORIAS ORDER BY 2";
    if( validateConnectionDB().equals("YES") ){
      connection.connect();
      try {
        st = connection.getCon().createStatement();
        rs = st.executeQuery(sentence);
        while (rs.next()) {
          JsonObject dataBrand = new JsonObject();
          dataBrand.addProperty("id", rs.getInt(1));
          dataBrand.addProperty("value", rs.getString(2));
          list.add(dataBrand);
        }
        reply = gson.toJson(list);
      } catch (SQLException e) {
        System.out.println(""+codigoReferenciaServicio+" - UNA EXCEPCIÓN OCURRIÓ AL LISTAR LAS MARCAS :"+e);
        connection.disconnect();
      } 
      connection.disconnect();
    }else{
      String[] dataNCDB = {"NCDB"};
      reply = gson.toJson(dataNCDB);
    }    
    return reply;
  }
  
  public String add_category(String jsonData){
    Conexion connection = new Conexion();
    Statement st = null;
    ResultSet rs = null;
    Gson gson = new Gson();
    String[] dataNO = {"NO"};
    String reply = gson.toJson(dataNO);
    String[] replyYES = {"YES"};
    String category = "["+jsonData+"]";
    String name = "", user = "";
    JsonParser parser = new JsonParser();
    JsonArray gsonArr = parser.parse(category).getAsJsonArray();
    for( JsonElement obj: gsonArr ){
      JsonObject gsonObj = obj.getAsJsonObject();
      name = gsonObj.get("name").getAsString();
      user = gsonObj.get("user").getAsString();
    }
    if( validateConnectionDB().equals("YES") ){
      connection.connect();
      try {
        String sentenceGetId = "SELECT \n" +
                               "    CASE WHEN MAX(ID) IS NULL THEN 1\n" +
                               "    ELSE MAX(ID) + 1\n" +
                               "    END ID\n" +
                               "FROM CATEGORIAS";
        st = connection.getCon().createStatement();
        rs = st.executeQuery(sentenceGetId);
        if( rs.next() ){
          int unicId = rs.getInt(1);
          String sentenceAddCategory = "INSERT INTO CATEGORIAS VALUES("+unicId+",'"+name+"','A',SYSDATE(),'"+user+"')";
          PreparedStatement preparedStmt = connection.getCon().prepareStatement(sentenceAddCategory);
          preparedStmt.execute();
          reply = gson.toJson(replyYES);
        }  
      } catch (SQLException e) {
        System.out.println(""+codigoReferenciaServicio+" - UNA EXCEPCIÓN OCURRIÓ AL GUARDAR LA CATEGORÍA :"+e);
        connection.disconnect();
      }
      connection.disconnect();
    }else{
      String[] dataNCDB = {"NCDB"};
      reply = gson.toJson(dataNCDB);
    } 
    return reply;
  }
  
  public String encryptProccess(String data){
    String dataEncrypt = null;
    try {
      dataEncrypt = encrypt(data);
    } catch (UnsupportedEncodingException ex) {
      Logger.getLogger(Servicios.class.getName()).log(Level.SEVERE, null, ex);
    }
    return dataEncrypt;
  }
  
  public String desencryptProccess(String data){
    String dataDesencerypt = null;
    try {
      dataDesencerypt = desencrypt(data);
    } catch (UnsupportedEncodingException ex) {
      Logger.getLogger(Servicios.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    return dataDesencerypt;
  }
  
  private static String encrypt(String s) throws UnsupportedEncodingException{
    return Base64.getEncoder().encodeToString(s.getBytes("utf-8"));
  }
  
  private static String desencrypt(String s) throws UnsupportedEncodingException{
    byte[] decode = Base64.getDecoder().decode(s.getBytes());
    return new String(decode, "utf-8");
  }
}
