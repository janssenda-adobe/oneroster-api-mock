package com.dm.onerosterapi.service.implementation;

import com.dm.onerosterapi.apiconfig.ApiMessageConfig;
import com.dm.onerosterapi.exceptions.ClassOfCourseNotFoundException;
import com.dm.onerosterapi.exceptions.ResourceNotFoundException;
import com.dm.onerosterapi.exceptions.SchoolNotFoundException;
import com.dm.onerosterapi.exceptions.UserNotFoundException;
import com.dm.onerosterapi.model.User;
import com.dm.onerosterapi.repository.dao.RosterDao;
import com.dm.onerosterapi.repository.jpa.UserRepository;
import com.dm.onerosterapi.service.interfaces.UserService;
import com.dm.onerosterapi.utility.AttributeTransformer;
import com.dm.onerosterapi.utility.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
@SuppressWarnings("unchecked")
public class UserServiceImpl implements UserService {

    final private Validator v;
    final private AttributeTransformer h;
    final private RosterDao rosterDao;
    final private UserRepository userRepository;


    @Inject
    public UserServiceImpl(
            AttributeTransformer h,
            RosterDao rosterDao,
            UserRepository userRepository,
            Validator v
    ) {
        this.v = v;
        this.h = h;
        this.rosterDao = rosterDao;
        this.userRepository = userRepository;
    }

    @Override
    public User getUserBySourcedId(String userId) throws UserNotFoundException {
        try {
            return (User) h.processResults(userRepository.findBySourcedIdIgnoreCase(userId));
        } catch (NullPointerException | ResourceNotFoundException e) {
            throw new UserNotFoundException(ApiMessageConfig.NO_USERS_FOR_ID + userId);
        }
    }

    @Override
    public User getStudentBySourcedId(String userId) throws UserNotFoundException {
        User u = getUserBySourcedId(userId);
        if (u.getRole().equals("student")) return u;
        else
            throw new UserNotFoundException(ApiMessageConfig.NOT_A_STUDENT + userId);
    }

    @Override
    public User getTeacherBySourcedId(String userId) throws UserNotFoundException {
        User u = getUserBySourcedId(userId);
        if (u.getRole().equals("teacher")) return u;
        else
            throw new UserNotFoundException(ApiMessageConfig.NOT_A_TEACHER + userId);
    }

    @Override
    public List<User> getAllUsers(String role, int offset, int limit) throws UserNotFoundException {
        try {
            return (List<User>) h.processResults(rosterDao.getAllUsersOfType(role, offset, limit));
        } catch (NullPointerException | ResourceNotFoundException e) {
            throw new UserNotFoundException(ApiMessageConfig.NO_RESULTS);
        }
    }

    @Override
    public List<User> getUsersByClass(String classSourcedId, String role, int offset, int limit) throws UserNotFoundException, ClassOfCourseNotFoundException {
        try {
            v.validateClass(classSourcedId);
            return (List<User>) h.processResults(rosterDao.getUsersByClass(classSourcedId, role, offset, limit));
        } catch (NullPointerException | ResourceNotFoundException e) {
            throw new UserNotFoundException(ApiMessageConfig.NO_USERS_FOR_CLASS + classSourcedId);
        }
    }

    @Override
    public List<User> getUsersBySchool(String schoolId, String role, int offset, int limit) throws UserNotFoundException, SchoolNotFoundException {
        try {
            v.validateSchool(schoolId);
            return (List<User>) h.processResults(rosterDao.getUsersBySchool(schoolId, role, offset, limit));
        } catch (NullPointerException | ResourceNotFoundException e) {
            throw new UserNotFoundException(ApiMessageConfig.NO_USERS_FOR_SCHOOL + schoolId);
        }
    }

    @Override
    public List<User> getUsersForClassInSchool(String classId, String schoolId, String role, int offset, int limit) throws UserNotFoundException,
            ClassOfCourseNotFoundException,
            SchoolNotFoundException {

        try {
            v.validateClass(classId);
            v.validateSchool(schoolId);
            return (List<User>) h.processResults(rosterDao.getUsersForClassInSchool(classId, schoolId, role, offset, limit));
        } catch (NullPointerException | ResourceNotFoundException e) {
            throw new UserNotFoundException(ApiMessageConfig.NO_RESULTS);
        }

    }


}

