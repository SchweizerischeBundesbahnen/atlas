package ch.sbb.atlas.workflow.model;

import ch.sbb.atlas.model.Status;

public interface AtlasVersionSnapshotable {

  Status getStatus();

  void setStatus(Status status);

  Long getWorkflowId();

  void setWorkflowId(Long id);

  Long getParentObjectId();

  void setParentObjectId(Long id);
}
