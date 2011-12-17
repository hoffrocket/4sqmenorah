package com.foursquare.examples.push;

import java.io.IOException;
import java.io.PrintWriter;

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
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    if (req.getPathInfo().contains("connect")){
      PersistenceManager pm = Common.getPM();
      try {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");
        PrintWriter out = resp.getWriter();
        User googler = Common.getGoogleUser();
        if (googler == null) {
          out.println("{\"authed\":false, \"loginUrl\":\"" + Common.getGoogleLoginUrl() +"\"}");
        }
        else {
          FoursquareApi api = null;
          String oauthToken = req.getParameter("access_token");
          if (oauthToken != null) {
            api = Common.getApi(oauthToken);
            LinkedUser u = LinkedUser.loadOrCreate(pm, Common.getGoogleUser().getUserId());
            u.foursquareAuth = api.getOAuthToken();
            u.save(pm);
          } else api = Common.getCurrentApi(pm);
        
          if (api == null || api.getOAuthToken() == null || api.getOAuthToken().length() <= 0) {
             out.println("{\"authed\":false, \"connectUrl\":\"" + Common.getFoursquareLoginUrl() +"\"}");
          } else {
            out.println("{\"authed\":true, \"token\":\"" 
                +  Common.createChannelToken(Common.TARGET_VENUE) +"\"" + 
                ",\"vid\":\"" + Common.TARGET_VENUE + "\"" +
                ",\"venuename\":\"" + Common.TARGET_VENUE_NAME + "\"" +
                "}");
          }
        }
      } finally {
        pm.close();
      }
    }
  }
}
