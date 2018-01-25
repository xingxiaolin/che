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

-- constraints
ALTER TABLE gzprojectconfig ADD CONSTRAINT fk_gzprojectconfig_gzprojects_id FOREIGN KEY (gzprojects_id) REFERENCES workspaceconfig (id);
ALTER TABLE gzprojectconfig ADD CONSTRAINT fk_gzprojectconfig_source_id FOREIGN KEY (source_id) REFERENCES sourcestorage (id);
CREATE INDEX index_gzprojectconfig_gzprojectsid ON gzprojectconfig (gzprojects_id);
CREATE INDEX index_gzprojectconfig_gzsourceid ON gzprojectconfig (source_id);
--------------------------------------------------------------------------------
