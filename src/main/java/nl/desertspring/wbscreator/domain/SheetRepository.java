/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.desertspring.wbscreator.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jcr.*;

/**
 *
 * @author sihaya
 */
public class SheetRepository {

    private Repository repository;

    public List<Sheet> findByProjectId(String projectId) {
        Session session = null;

        try {
            session = SessionUtil.login(repository);

            return handleFindByProjectId(session, projectId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        finally {
            SessionUtil.logout(session);
        }
    }

    private Sheet constructSheet(Node node) throws RepositoryException {
        Sheet sheet = new Sheet();
        sheet.setName(node.getName());
        sheet.setSheetId(node.getIdentifier());
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
