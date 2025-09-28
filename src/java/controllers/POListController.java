package controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.PurchaseOrder;
import models.User;
import dao.PurchaseOrderDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class POListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("POListController: doGet called - " + request.getRequestURI());
        
        HttpSession session = request.getSession();
        
        // Check authentication and inventory role
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("POListController: No user in session, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        System.out.println("POListController: User found - " + currentUser.getFullName() + ", RoleID = " + currentUser.getRoleID());
        
        // Check if user has Inventory role (RoleID = 3)
        if (currentUser.getRoleID() != 3) {
            System.out.println("POListController: Access denied, user roleID = " + currentUser.getRoleID());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Inventory privileges required.");
            return;
        }
        
        // Get page parameters
        int page = 1;
        int recordsPerPage = 10;
        
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        // Get filter parameters
        String statusFilter = request.getParameter("status");
        String supplierFilter = request.getParameter("supplier");
        String searchKeyword = request.getParameter("search");
        
        // Sử dụng DAO để lấy dữ liệu thật từ database
        PurchaseOrderDAO poDAO = new PurchaseOrderDAO();
        List<PurchaseOrder> purchaseOrders = poDAO.getPurchaseOrdersWithFilters(statusFilter, supplierFilter, searchKeyword);
        
        // Lấy danh sách suppliers cho dropdown
        List<java.util.Map<String, Object>> suppliers = poDAO.getAllSuppliers();
        
        // Calculate pagination
        int totalRecords = purchaseOrders.size();
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
        int startIndex = (page - 1) * recordsPerPage;
        int endIndex = Math.min(startIndex + recordsPerPage, totalRecords);
        
        List<PurchaseOrder> paginatedPOs = new ArrayList<PurchaseOrder>();
        if (startIndex < totalRecords) {
            paginatedPOs = purchaseOrders.subList(startIndex, endIndex);
        }
        
        // Set attributes for JSP
        request.setAttribute("purchaseOrders", paginatedPOs);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("supplierFilter", supplierFilter);
        request.setAttribute("searchKeyword", searchKeyword);
        request.setAttribute("suppliers", suppliers);
        
        // Forward to JSP
        request.getRequestDispatcher("/WEB-INF/views/inventory/po-list.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}