package controller;

import model.User;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import beans.Product;
import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author simorgh
 */
public class ServletDispatcher extends HttpServlet {
    private DataManager data;
    
    @Override
    public void init() throws ServletException {
	super.init();
        loadState();
    }
    
    @Override
    public void destroy() {
        saveState();
    }
    

    ////////////////////////////////////////////////////////
    //                     PERSISTENCE
    ////////////////////////////////////////////////////////
   
    private void saveState(){
        System.out.println("@saveState()");
        String path = getServletContext().getRealPath("/WEB-INF/users.json").replace("/build","");
        try {  
            this.data.saveUsers(path);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServletDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    private void loadState() {
        System.out.println("@loadState()");
        
        ServletContext c = getServletContext();
        String users = c.getRealPath("/WEB-INF/users.json");
	String products = c.getRealPath("/WEB-INF/products.json");
	this.data = DataManager.getInstance(users, products);
    }
    
    
    ////////////////////////////////////////////////////////
    //                      SERVLET
    ////////////////////////////////////////////////////////
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        locationProxy(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        locationProxy(request, response);
        //TODO. Implement POST-Redirect-GET Pattern Design.
    }

    /**
     * Returns a short description of the servlet.
     * 
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
    
    
    ////////////////////////////////////////////////////////////////
    //                       LOCATIONS
    ////////////////////////////////////////////////////////////////
    public void locationProxy(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String CONTEXT = request.getContextPath();
        String location = request.getRequestURI();
	

        if(location.equals(CONTEXT + "/cataleg")) {
            String user = request.getRemoteUser();
            if(user != null) DataManager.getUsers().get(user);
            showCataleg(request, response);
            
        } else if (location.equals(CONTEXT + "/protegit/llista")) {
            String name = request.getRemoteUser();
            User u;
            if(!DataManager.getUsers().containsKey(name)) {
                u = new User(name, 500.0f);
                data.addUser(u);    // user needs to be added for persistence purposes
            }
	    showPurchases(request, response);
            
        } else if (location.equals(CONTEXT + "/afegir")) {
            // User recovery
            String name = request.getRemoteUser();
            User u;
            if(DataManager.getUsers().containsKey(name)) u = DataManager.getUsers().get(name);
            else {
                u = new User(name, 500.0f);
                data.addUser(u);    // user needs to be added for persistence purposes
            }
            
            // Add product to cart
            String pid = request.getParameter("item");
            Product p = DataManager.getProducts().get(pid);
            if(!u.getCart().contains(p) && !u.getProducts().contains(p)) {
                System.out.println(u.getName() + " adding item " + p.getName() + " to cart...");
                u.addToCart(p);
            }
            
            System.out.println("CART ITEMS: " + u.getCart().size());
            request.getSession().setAttribute("cart", u.getCart().size());
            showCataleg(request, response);
            
        }else if (location.contains(CONTEXT + "/protegit/comprar")) {    
            buyResource(request,response);
            showPurchases(request, response);
            
        } else if (location.contains("/download")) {
            downloadResource(request, response);
            
        } else if (location.contains("logout")) {
            request.getSession().invalidate();
            showPage(request, response, "/index.jsp");
            
        } else if (location.equals(CONTEXT + "/consulta")) {    
                showPage(request, response, "/consulta.jsp");
            
            } else {
	    showPage(request, response, "/error404.jsp");
	}
    }


    ////////////////////////////////////////////////////////////////
    //                          PAGES
    ////////////////////////////////////////////////////////////////
    
    /**
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    private void showCataleg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ArrayList<Product> books = new ArrayList();
	ArrayList<Product> audio = new ArrayList();
	ArrayList<Product> video = new ArrayList();
        
	for (Product p : DataManager.getProducts().values()) {
            if(p.getType()==DataManager.FileType.BOOK) books.add(p);
            else if (p.getType()==DataManager.FileType.AUDIO) audio.add(p);
            else if (p.getType()==DataManager.FileType.VIDEO) video.add(p); 
	}
        
        request.setAttribute("books", books);
	request.setAttribute("audio", audio);
	request.setAttribute("video", video); 
	showPage(request, response, "/WEB-INF/jsp/cataleg.jsp");
    }
    
    /**
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    private void showPurchases(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ConcurrentHashMap <String, User> users = DataManager.getUsers();
        if(users.containsKey(request.getRemoteUser())){
            request.setAttribute("purchased", users.get(request.getRemoteUser()).getProducts());
            request.setAttribute("cart", users.get(request.getRemoteUser()).getCart());
        }
        showPage(request, response, "/WEB-INF/jsp/protected/llista.jsp");
    }

    /**
     * 
     * @param request
     * @param response
     * @param jspPage
     * @throws ServletException
     * @throws IOException 
     */
    public void showPage(HttpServletRequest request, HttpServletResponse response, String jspPage) throws ServletException, IOException{
        ServletContext sc = getServletContext();
        RequestDispatcher rd = sc.getRequestDispatcher(jspPage);
        rd.forward(request, response);
    }
 
    /**
     * 
     * @param request
     * @param response
     * @throws IOException 
     */
    private void buyResource(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getRemoteUser();
        User u;
        if(DataManager.getUsers().containsKey(name)) u = DataManager.getUsers().get(name);
        else {
            u = new User(name, 500.0f);
            data.addUser(u); // user needs to be added for persistence purposes
        }
        
        //String pid = (String) request.getAttribute("param");
        String pid = request.getParameter("pid");
        
        Product p = DataManager.getProducts().get(pid);
        float price = p.getPrice();
        if(!(price > u.getCredits())){
            u.addToPurchased(p);
            u.removeFromCart(p);
            u.setCredits(u.getCredits() - price);
            System.out.println("Item "+ p.getName() +" ha sido comprado;" + " Dispones de "+ u.getCredits()+" creditos");
        } else  System.out.println("No hay saldo suficiente para comprar Item "+ p.getName());
    } 
  /**
   * 
   * @param request
   * @param response
   * @throws IOException 
   */
    private void downloadResource(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pid = request.getParameter("pid");
        Product p = DataManager.getProducts().get(pid);
        
        String apath = this.getServletContext().getRealPath("/WEB-INF/");
        System.out.println(apath + p.getPath());
        File file = new File(apath + p.getPath());
        
        ServletOutputStream outStream = response.getOutputStream();
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        byte[] byteBuffer = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int length;
        while ((length = in.read(byteBuffer)) != -1) {
            outStream.write(byteBuffer, 0, length);
        }
        in.close();
        outStream.close();
    }   
  
}