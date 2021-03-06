package no.ic.cloud.blog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class RussianArmyBlog extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RussianArmyBlog.class);

    private static final BlogStore STORE = new S3BlogStore();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF8");
        String thread = req.getParameter("thread");
        if (thread != null && thread.trim().length() > 0) {
            String post = req.getParameter("post");
            List<String> posts;
            try {
                STORE.getPosts(thread);
            } catch (RuntimeException ex) {
                STORE.addThread(thread);
                logger.debug("Added thread " + thread);
            }
            if (post != null && post.trim().length() > 0) {
                STORE.addPost(thread, post);
                logger.debug("Added post " + post + " to thread " + thread);
            }
            posts = STORE.getPosts(thread);
            logger.debug("Printing posts for thread " + thread);
            printThread(resp.getWriter(), thread, posts);
        } else {
            logger.debug("Printing threads");
            printThreads(resp.getWriter(), STORE.getThreads());
        }
    }

    private static void printThreads(PrintWriter writer, List<String> threads) {
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
        writer.println("</head>");
        writer.println("<body bgcolor=\"red\">");
        writer.println("<img src=\"flag.gif\">");
        writer.println("<img src=\"arms.gif\">");
        writer.println("<h2>Блог русской армии Федерации</h2>");
        writer.println("<form action=\"\" method=\"get\">");
        writer.println("<input type=\"text\" name=\"thread\" size=\"100\">");
        writer.println("<input type=\"submit\" value=\"Создать\">");
        writer.println("</form>");
        writer.println("<ul>");
        for (String thread : threads) {
            writer.println("<li><a href=\"?thread=" + thread + "\">" + thread + "</a></li>");
        }
        writer.println("</ul>");
        writer.println("<img src=\"serve.gif\">");
        writer.println("<embed src=\"anthem.wav\" autostart=true loop=true>");
        writer.println("</body>");
        writer.println("</html>");
    }

    private static void printThread(PrintWriter writer, String thread, List<String> posts) {
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
        writer.println("</head>");
        writer.println("<body bgcolor=\"red\">");
        writer.println("<img src=\"flag.gif\">");
        writer.println("<img src=\"arms.gif\">");
        writer.println("<h2>Блог русской армии Федерации</h2>");
        writer.println("<h3>Тема: " + thread + "</h3>");
        writer.println("<a href=\"?thread=\">Все темы</a>");
        writer.println("<form action=\"\" method=\"get\">");
        writer.println("<input type=\"text\" name=\"post\" size=\"100\">");
        writer.println("<input type=\"submit\" value=\"Сообщение\">");
        writer.println("<input type=\"hidden\" name=\"thread\" value=\"" + thread + "\">");
        writer.println("</form>");
        writer.println("<ul>");
        for (String post : posts) {
            writer.println("<li>" + post + "</li>");
        }
        writer.println("</ul>");
        writer.println("<img src=\"serve.gif\">");
        writer.println("<embed src=\"anthem.wav\" autostart=true loop=true>");
        writer.println("</body>");
        writer.println("</html>");
    }

}
