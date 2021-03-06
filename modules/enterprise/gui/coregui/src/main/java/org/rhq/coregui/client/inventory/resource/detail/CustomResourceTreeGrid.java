/*
 * RHQ Management Platform
 * Copyright (C) 2005-2010 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.rhq.coregui.client.inventory.resource.detail;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

import org.rhq.core.domain.resource.Resource;
import org.rhq.coregui.client.ImageManager;
import org.rhq.coregui.client.inventory.resource.detail.ResourceTreeDatasource.AutoGroupTreeNode;
import org.rhq.coregui.client.inventory.resource.detail.ResourceTreeDatasource.ResourceTreeNode;

/**
 * @author Greg Hinkle
 */
public class CustomResourceTreeGrid extends TreeGrid {

    public CustomResourceTreeGrid() {
        super();
    }

    @Override
    protected String getIcon(Record record, boolean defaultState) {

        if (record instanceof TreeNode) {
            boolean open = getTree().isOpen((TreeNode) record);

            if (record instanceof ResourceTreeNode) {
                ResourceTreeNode node = (ResourceTreeNode) record;

                if (node.isLocked()) {
                    return ImageManager.getLockedIcon();
                } else {
                    Resource resource = ((ResourceTreeDatasource.ResourceTreeNode) record).getResource();
                    return ImageManager.getResourceIcon(resource.getResourceType().getCategory(), resource
                        .getCurrentAvailability().getAvailabilityType());
                }
            } else if (record instanceof AutoGroupTreeNode) {
                return "resources/folder_autogroup_" + (open ? "opened" : "closed") + ".png";
            } else {
                // note - subcategory nodes use default folder icons
                return super.getIcon(record, defaultState);
            }
        }
        return null;
    }

}
