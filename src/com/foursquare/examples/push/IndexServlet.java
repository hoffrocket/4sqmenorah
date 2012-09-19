package com.foursquare.examples.push;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.foursquare.examples.push.models.LinkedUser;
import com.foursquare.examples.push.util.Common;
import com.google.appengine.api.users.User;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;

/**
 * A servlet to dynamically create the HTML for the homepage.
 * Also enforces that the user is both logged in to Google and connected to Foursquare.
 */
@SuppressWarnings("serial")
public class IndexServlet extends HttpServlet {
  private static final Logger log = Logger.getLogger(IndexServlet.class.getName());
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    if (req.getPathInfo().contains("connect")){
      PersistenceManager pm = Common.getPM();
      try {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        User googler = Common.getGoogleUser();
        log.info("got user " + googler);
        if (googler == null) {
          out.println("{\"authed\":false, \"loginUrl\":\"" + Common.getGoogleLoginUrl() +"\"}");
        }
        else {
          
          String oauthToken = req.getParameter("access_token");
          String vid = req.getParameter("vid");
          LinkedUser u = LinkedUser.loadOrCreate(pm, Common.getGoogleUser().getUserId());
          log.info("got linked user " + u);
          if (vid != null) {
            log.info("Saving vid to user persistence");
            u.vid = vid;
            u.save(pm);
          } else {
            vid = u.vid;
          }
          
          if (oauthToken != null) {
            u.foursquareAuth = oauthToken;
            u.save(pm);
          } else {
            oauthToken = u.foursquareAuth;
          }
        
          if (oauthToken == null || oauthToken.length() <= 0) {
             out.println("{\"authed\":false, \"connectUrl\":\"" + Common.getFoursquareLoginUrl() +"\"}");
          } else {
            out.println("{\"authed\":true, \"token\":\"" 
                +  Common.createChannelToken(vid) +"\"" + 
                (vid != null ? ",\"vid\":\"" + vid + "\"" : "" )+
                ",\"accessToken\":\"" + oauthToken + "\"" +
                "}");
          }
        }
      } finally {
        pm.close();
      }
    }
  }
}
