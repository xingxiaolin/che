--
-- Copyright (c) 2012-2017 Codenvy, S.A.
-- All rights reserved. This program and the accompanying materials
-- are made available under the terms of the Eclipse Public License v1.0
-- which accompanies this distribution, and is available at
-- http://www.eclipse.org/legal/epl-v10.html
--
-- Contributors:
--   Codenvy, S.A. - initial API and implementation
--

-- Project configuration -------------------------------------------------------
CREATE TABLE gzprojectconfig (
    id              BIGINT          NOT NULL,
    description     TEXT,
    name            VARCHAR(255),
    path            VARCHAR(255)    NOT NULL,
    type            VARCHAR(255),
    source_id       BIGINT,
    gzprojects_id     BIGINT,

    PRIMARY KEY (id)
);
CREATE INDEX index_gzprojectconfig_gzprojectsid ON gzprojectconfig (gzprojects_id);
-- constraints
ALTER TABLE gzprojectconfig ADD CONSTRAINT fk_gzprojectconfig_gzprojects_id FOREIGN KEY (gzprojects_id) REFERENCES workspaceconfig (id);
ALTER TABLE gzprojectconfig ADD CONSTRAINT fk_gzprojectconfig_source_id FOREIGN KEY (source_id) REFERENCES sourcestorage (id);
CREATE INDEX index_gzprojectconfig_sourceid ON gzprojectconfig (source_id);
ALTER TABLE projectattribute ADD CONSTRAINT fk_gzprojectattribute_dbattributes_id FOREIGN KEY (dbattributes_id) REFERENCES gzprojectconfig (id);

-- Project mixins --------------------------------------------------------------
CREATE TABLE gzprojectconfig_mixins (
    projectconfig_id    BIGINT,
    mixins              VARCHAR(255)
);
--constraints
ALTER TABLE gzprojectconfig_mixins ADD CONSTRAINT fk_gzprojectconfig_mixins_projectconfig_id FOREIGN KEY (projectconfig_id) REFERENCES gzprojectconfig (id);
