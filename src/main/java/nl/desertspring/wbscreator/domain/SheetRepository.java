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
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 *
 * @author sihaya
 */
public class SheetRepository {

    private Session session;

    public List<Sheet> findByProjectId(String projectId) {
        try {
            return handleFindByProjectId(projectId);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Sheet constructSheet(Node node) throws RepositoryException {
        Sheet sheet = new Sheet();
        sheet.setName(node.getName());
        return sheet;
    }

    private List<Sheet> handleFindByProjectId(String projectId) throws RepositoryException {
        Node node = session.getNodeByIdentifier(projectId);

        List<Sheet> result = new ArrayList<Sheet>();
        NodeIterator iter = node.getNodes();

        while (iter.hasNext()) {
            result.add(constructSheet(iter.nextNode()));
        }
        
        return result;
    }

    public void save(String projectId, Sheet sheet) {
        try {
            handleSave(projectId, sheet);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void handleSave(String projectId, Sheet sheet) throws Exception {
        Node projectNode = session.getNodeByIdentifier(projectId);

        Node sheetNode = projectNode.addNode(sheet.getName());
        sheet.setSheetId(sheetNode.getIdentifier());

        session.save();
    }

    public Sheet findById(String id) {
        try {
            return handleFindById(id);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Sheet handleFindById(String id) throws Exception {
        Node node = session.getNodeByIdentifier(id);
        Sheet sheet = constructSheet(node);

        return sheet;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
