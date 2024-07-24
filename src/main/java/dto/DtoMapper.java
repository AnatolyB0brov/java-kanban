package dto;

import task.Subtask;

public class DtoMapper {
    public static SubtaskDto getDtoFromSubtask(Subtask subtask) {
        Integer epicId = null;
        if (subtask.getEpicId() != null) {
            epicId = subtask.getEpicId();
        }
        return new SubtaskDto(subtask.getId(), subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                subtask.getDuration(), epicId, subtask.getStartTime());
    }
}
