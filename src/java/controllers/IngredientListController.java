package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Ingredient;
import models.User;
import dao.IngredientDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IngredientListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("IngredientListController: doGet called - " + request.getRequestURI());
        
        HttpSession session = request.getSession();
        
        // Check authentication and inventory role
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("IngredientListController: No user in session, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        System.out.println("IngredientListController: User found - " + currentUser.getFullName() + ", RoleID = " + currentUser.getRoleID());
        
        // Check if user has Inventory role (RoleID = 3)
        if (currentUser.getRoleID() != 3) {
            System.out.println("IngredientListController: Access denied, user roleID = " + currentUser.getRoleID());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Inventory privileges required.");
            return;
        }
        
        // Get page parameters
        int page = 1;
        int recordsPerPage = 15;
        
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        // Get filter parameters
        String stockFilter = request.getParameter("stock"); // low, normal, high
        String supplierFilter = request.getParameter("supplier");
        String unitFilter = request.getParameter("unit");
        String searchKeyword = request.getParameter("search");
        
        // Sử dụng DAO để lấy dữ liệu thật từ database
        IngredientDAO ingredientDAO = new IngredientDAO();
        List<Ingredient> ingredients = ingredientDAO.getIngredientsWithFilters(stockFilter, supplierFilter, unitFilter, searchKeyword);
        
        // Lấy danh sách suppliers cho dropdown
        List<java.util.Map<String, Object>> suppliers = ingredientDAO.getAllSuppliers();
        
        // Calculate pagination
        int totalRecords = ingredients.size();
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
        int startIndex = (page - 1) * recordsPerPage;
        int endIndex = Math.min(startIndex + recordsPerPage, totalRecords);
        
        List<Ingredient> paginatedIngredients = new ArrayList<Ingredient>();
        if (startIndex < totalRecords) {
            paginatedIngredients = ingredients.subList(startIndex, endIndex);
        }
        
        // Set attributes for JSP
        request.setAttribute("ingredients", paginatedIngredients);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("stockFilter", stockFilter);
        request.setAttribute("supplierFilter", supplierFilter);
        request.setAttribute("unitFilter", unitFilter);
        request.setAttribute("searchKeyword", searchKeyword);
        request.setAttribute("suppliers", suppliers);
        
        // Forward to JSP
        request.getRequestDispatcher("/WEB-INF/views/inventory/ingredient-list.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}