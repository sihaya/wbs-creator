/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import org.apache.jackrabbit.commons.webdav.QueryUtil;
import org.apache.jackrabbit.util.Text;

/**
 *
 * @author sihaya
 */
public class SheetRepository {

    private static final String ATTR_PUBLIC_SECRET = "publicSecret";
    private Repository repository;

    public List<Sheet> findByProjectId(String projectId) {
        Session session = null;

        try {
            session = SessionUtil.login(repository);

            return handleFindByProjectId(session, projectId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }

    private Sheet constructSheet(Node node) throws RepositoryException {
        Sheet sheet = new Sheet();
        sheet.setName(node.getName());
        sheet.setSheetId(node.getIdentifier());

        if (node.hasProperty(ATTR_PUBLIC_SECRET)) {
            sheet.setPublicSecret(node.getProperty(ATTR_PUBLIC_SECRET).getString());
        }

        return sheet;
    }

    private List<Sheet> handleFindByProjectId(Session session, String projectId) throws RepositoryException {
        Node node = session.getNodeByIdentifier(projectId);

        List<Sheet> result = new ArrayList<Sheet>();
        NodeIterator iter = node.getNodes();

        while (iter.hasNext()) {
            Node sheetNode = iter.nextNode();
            if (sheetNode.getName().equals("members")) {
                continue;
            }

            result.add(constructSheet(sheetNode));
        }

        return result;
    }

    public void save(String projectId, Sheet sheet) {
        Session session = null;

        try {
            session = SessionUtil.login(repository);

            handleSave(session, projectId, sheet);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }

    private void handleSave(Session session, String projectId, Sheet sheet) throws Exception {
        Node projectNode = session.getNodeByIdentifier(projectId);

        Node sheetNode = projectNode.addNode(sheet.getName());
        sheet.setSheetId(sheetNode.getIdentifier());

        session.save();
    }

    public Sheet findById(String id) {
        Session session = null;

        try {
            session = SessionUtil.login(repository);

            return handleFindById(session, id);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }

    public void update(Sheet sheet) {
        Session session = null;

        try {
            session = SessionUtil.login(repository);

            Node node = session.getNodeByIdentifier(sheet.getSheetId());

            if (sheet.getPublicSecret() != null) {
                node.setProperty(ATTR_PUBLIC_SECRET, sheet.getPublicSecret());
            }

            session.save();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }

    public Sheet findByPublicSecret(String publicSecret) {
        Session session = null;

        try {
            session = SessionUtil.login(repository);

            Query query = session.getWorkspace().getQueryManager().createQuery(
                    "//*[@publicSecret = '" + Text.escapeIllegalJcrChars(publicSecret) + "']", Query.XPATH);

            NodeIterator result = query.execute().getNodes();

            if (result.hasNext()) {
                return constructSheet(result.nextNode());
            }

            return null;
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        } finally {
            SessionUtil.logout(session);
        }
    }

    private Sheet handleFindById(Session session, String id) throws Exception {
        Node node = session.getNodeByIdentifier(id);
        Sheet sheet = constructSheet(node);

        return sheet;
    }

    @Resource(name = ResourceConstants.REPOSITORY)
    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
