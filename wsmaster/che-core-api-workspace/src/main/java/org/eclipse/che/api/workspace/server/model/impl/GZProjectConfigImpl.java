/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.workspace.server.model.impl;

import org.eclipse.che.api.core.model.project.GZProjectConfig;
import org.eclipse.che.api.core.model.project.SourceStorage;
//import org.eclipse.jetty.util.log.Log;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.stream.Collectors.toMap;

/**
 * Data object for {@link GZProjectConfig}.
 *
 * @author Eugene Voevodin
 * @author Dmitry Shnurenko
 */
@Entity(name = "GZProjectConfig")
@Table(name = "gzprojectconfig")
public class GZProjectConfigImpl implements GZProjectConfig {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type = "gzproject";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "source_id")
    private SourceStorageImpl source;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "gzprojectconfig_mixins", joinColumns = @JoinColumn(name = "projectconfig_id"))
    @Column(name = "mixins")
    private List<String> mixins;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    @JoinColumn(name = "dbattributes_id")
//    @MapKey(name = "name")
//    private Map<String, GZAttribute> dbAttributes;

    // Mapping delegated to 'dbAttributes' field
    // as it is impossible to map nested list directly
    @Transient
    private Map<String, List<String>> attributes;

    public GZProjectConfigImpl() {}

    /**
     * 根据ProjectConfig构造
     * @param config
     */
    public GZProjectConfigImpl(GZProjectConfig config) {
        name = config.getName();
        path = config.getPath();
        description = config.getDescription();
        type = config.getType();
        mixins = new ArrayList<>(config.getMixins());
        attributes = config.getAttributes()
                                  .entrySet()
                                  .stream()
                                  .collect(toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue())));
        SourceStorage sourceStorage = config.getSource();
        if (sourceStorage != null) {
            source = new SourceStorageImpl(sourceStorage.getType(), sourceStorage.getLocation(), sourceStorage.getParameters());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public List<String> getMixins() {
        if (mixins == null) {
            mixins = new ArrayList<>();
        }
        return mixins;
    }

    public void setMixins(List<String> mixins) {
        this.mixins = mixins;
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        return attributes;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    @Override
    public SourceStorageImpl getSource() {
        return source;
    }

    public void setSource(SourceStorageImpl sourceStorage) {
        this.source = sourceStorage;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GZProjectConfigImpl)) {
            return false;
        }
        final GZProjectConfigImpl that = (GZProjectConfigImpl)obj;
        return Objects.equals(id, that.id)
               && Objects.equals(path, that.path)
               && Objects.equals(name, that.name)
               && Objects.equals(type, that.type)
               && Objects.equals(description, that.description)
               && Objects.equals(source, that.source)
               && getMixins().equals(that.getMixins())
               && getAttributes().equals(that.getAttributes());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(id);
        hash = 31 * hash + Objects.hashCode(path);
        hash = 31 * hash + Objects.hashCode(name);
        hash = 31 * hash + Objects.hashCode(type);
        hash = 31 * hash + Objects.hashCode(description);
        hash = 31 * hash + Objects.hashCode(source);
        hash = 31 * hash + getMixins().hashCode();
        hash = 31 * hash + getAttributes().hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "GZProjectConfigImpl{" +
               "id=" + id +
               ", path='" + path + '\'' +
               ", name='" + name + '\'' +
               ", type='" + type + '\'' +
               ", description='" + description + '\'' +
               ", source=" + source +
               ", mixins=" + mixins +
               ", attributes=" + attributes +
               '}';
    }
    /**
     * Synchronizes instance attributes with db attributes,
     * should be called by internal components in needed places,
     * this can't be done neither by {@link PrePersist} nor by {@link PreUpdate}
     * as when the entity is merged the transient attribute won't be passed
     * to event handlers.
     */
//    public void prePersistAttributes() {
//        if (dbAttributes == null) {
//            dbAttributes = new HashMap<>();
//        }
//        final Map<String, GZAttribute> dbAttrsCopy = new HashMap<>(dbAttributes);
//        dbAttributes.clear();
//        for (Map.Entry<String, List<String>> entry : getAttributes().entrySet()) {
//            GZAttribute attribute = dbAttrsCopy.get(entry.getKey());
//            if (attribute == null) {
//                attribute = new GZAttribute(entry.getKey(), entry.getValue());
//            } else if (!Objects.equals(attribute.values, entry.getValue())) {
//                attribute.values = entry.getValue();
//            }
//            dbAttributes.put(entry.getKey(), attribute);
//        }
//    }

//    @PostLoad
//    @PostUpdate
//    @PostPersist
//    private void postLoadAttributes() {
//        if (dbAttributes != null) {
//            attributes = dbAttributes.values()
//                                     .stream()
//                                     .collect(toMap(attr -> attr.name, attr -> attr.values));
//        }
//    }

//    @Entity(name = "GZProjectAttribute")
//    @Table(name = "projectattribute")
//    private static class GZAttribute {
//
//        @Id
//        @GeneratedValue
//        @Column(name = "id")
//        private Long id;
//
//        @Column(name = "name")
//        private String name;
//
//        @ElementCollection(fetch = FetchType.EAGER)
//        @CollectionTable(name = "projectattribute_values", joinColumns = @JoinColumn(name = "projectattribute_id"))
//        @Column(name = "values")
//        private List<String> values;
//
//        public GZAttribute() {}
//
//        public GZAttribute(String name, List<String> values) {
//            this.name = name;
//            this.values = values;
//        }
//
//        @Override
//        public boolean equals(Object obj) {
//            if (this == obj) {
//                return true;
//            }
//            if (!(obj instanceof GZAttribute)) {
//                return false;
//            }
//            final GZAttribute that = (GZAttribute)obj;
//            return Objects.equals(id, that.id)
//                   && Objects.equals(name, that.name)
//                   && values.equals(that.values);
//        }
//
//        @Override
//        public int hashCode() {
//            int hash = 7;
//            hash = 31 * hash + Objects.hashCode(id);
//            hash = 31 * hash + Objects.hashCode(name);
//            hash = 31 * hash + values.hashCode();
//            return hash;
//        }
//
//        @Override
//        public String toString() {
//            return "GZAttribute{" +
//                   "values=" + values +
//                   ", name='" + name + '\'' +
//                   ", id=" + id +
//                   '}';
//        }
//    }
}
