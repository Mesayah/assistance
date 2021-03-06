package pl.mesayah.assistance.task;

import pl.mesayah.assistance.AbstractFilterableEntity;
import pl.mesayah.assistance.milestone.Milestone;
import pl.mesayah.assistance.project.Project;
import pl.mesayah.assistance.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Something to do to develop a {@link Project}.
 * <p>
 * Task is a unit of work to be done in order to develop a project. They can be a part of milestones and can
 * have children tasks (subtasks) and one parent.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Task extends AbstractFilterableEntity implements Serializable {


    /**
     * An unique identifier of this task.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * A NAME of this task.
     */
    private String name;

    /**
     * Users responsible for completing this task.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_task",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedUsers;

    /**
     * A stage of progress this task is in.
     */
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    /**
     * Milestones this task is a part of.
     */
    @ManyToMany
    @JoinTable(
            name = "milestone_task",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "milestone_id")
    )
    private List<Milestone> milestones;

    /**
     * Importance of this task.
     */
    @Enumerated(EnumType.ORDINAL)
    private Priority priority;

    /**
     * A date when this task should be completed at the latest.
     */
    private LocalDate deadline;

    /**
     * A description of this task, what it is about.
     */
    @Column(length = 4095)
    private String description;

    /**
     * A task this task is a subtask of.
     */
    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    /**
     * Subtasks of this task.
     */
    @OneToMany(mappedBy = "parentTask", fetch = FetchType.EAGER)
    private Set<Task> subtasks;

    /**
     * A type of this task.
     */
    @Enumerated(EnumType.ORDINAL)
    private Type type;


    /**
     * A project this task involves.
     */
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;


    /**
     * Constructs a task object with no attributes specified.
     */
    public Task() {

        this.status = Status.WAITING;
        this.type = Type.TASK;
        this.priority = Priority.MEDIUM;
        this.subtasks = new HashSet<>();
    }


    /**
     * @return a list of milestones this task is a part of
     */
    public List<Milestone> getMilestones() {

        return milestones;
    }


    /**
     * @param milestones a list of milestones for this task to be a part of
     */
    public void setMilestones(List<Milestone> milestones) {

        this.milestones = milestones;
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(assignedUsers, task.assignedUsers) &&
                status == task.status &&
                priority == task.priority &&
                Objects.equals(deadline, task.deadline) &&
                Objects.equals(description, task.description) &&
                Objects.equals(parentTask, task.parentTask) &&
                Objects.equals(subtasks, task.subtasks) &&
                type == task.type &&
                Objects.equals(project, task.project);
    }


    /**
     * @return a set of tasks being subtasks of this task
     */
    public Set<Task> getSubtasks() {

        return subtasks;
    }


    /**
     * @param subtasks a set of tasks to be a subtasks for this task
     */
    public void setSubtasks(Set<Task> subtasks) {

        this.subtasks = subtasks;
    }


    /**
     * @return a project this task is defined for
     */
    public Project getProject() {

        return project;
    }


    /**
     * @param project a project this task is to be defined for
     */
    public void setProject(Project project) {

        this.project = project;
    }


    /**
     * @return an unique identifier of this task
     */
    @Override
    public Long getId() {

        return id;
    }

    /**
     * @param id an unique identifier for this task
     */
    public void setId(Long id) {

        this.id = id;
    }


    /**
     * @return the NAME of this task
     */
    public String getName() {

        return name;
    }


    /**
     * @param name a NAME for this task
     */
    public void setName(String name) {

        this.name = name;
    }


    /**
     * @return set of users responsible for completing this task
     */
    public Set<User> getAssignedUsers() {

        return assignedUsers;
    }


    /**
     * @param assigneeUsers a set of users to be responsible for completing this task
     */
    public void setAssignedUsers(Set<User> assigneeUsers) {

        this.assignedUsers = assigneeUsers;
    }


    /**
     * @return a progress status of this task
     */
    public Status getStatus() {

        return status;
    }


    /**
     * @param status a progress status for this task
     */
    public void setStatus(Status status) {

        this.status = status;
    }


    /**
     * @return a priority of this task
     */
    public Priority getPriority() {

        return priority;
    }


    /**
     * @param priority a priority for this task
     */
    public void setPriority(Priority priority) {

        this.priority = priority;
    }


    /**
     * @return a deadline date of this task
     */
    public LocalDate getDeadline() {

        return deadline;
    }


    /**
     * @param deadline a deadline date for this task
     */
    public void setDeadline(LocalDate deadline) {

        this.deadline = deadline;
    }


    /**
     * @return a description of this task
     */
    public String getDescription() {

        return description;
    }


    /**
     * @param description a description for this task
     */
    public void setDescription(String description) {

        this.description = description;
    }


    /**
     * @return a parent task of this task
     */
    public Task getParentTask() {

        return parentTask;
    }


    /**
     * @param parentTask a parent task for this task
     */
    public void setParentTask(Task parentTask) {

        this.parentTask = parentTask;
    }


    /**
     * @return a type of this task
     */
    public Type getType() {

        return type;
    }


    /**
     * @param type a type for this task
     */
    public void setType(Type type) {

        this.type = type;
    }


    @Override
    public String getTextRepresentation() {

        return name;
    }

    @Override
    public String toString() {
        return "("+ id + ") " + name;
    }


    /**
     * Describes a nature of a task.
     */
    public enum Type {
        TASK("Task"),
        BUG("Bug"),
        FEATURE("Feature"),
        COSMETICS("Cosmetics"),
        USER_STORY("User Story");

        private final String name;


        Type(String name) {

            this.name = name;
        }


        @Override
        public String toString() {

            return name;
        }
    }

    /**
     * Describes a stage of progress.
     */
    public enum Status {
        WAITING("Waiting"),
        TO_DO("To Do"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        VERIFIED("Verified");

        private final String name;


        Status(String name) {

            this.name = name;
        }


        @Override
        public String toString() {

            return name;
        }
    }

    /**
     * Importance of something.
     */
    public enum Priority {
        HIGHEST("Critical"),
        HIGH("High"),
        MEDIUM("Normal"),
        LOW("Minor"),
        LOWEST("Low");

        private final String name;


        Priority(String name) {

            this.name = name;
        }


        @Override
        public String toString() {

            return name;
        }
    }
}

