package com.bp.attendease.db.repos

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class AppRepository(private val application: Application) {

    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    suspend fun createClassroom(
        teacherUid: String,
        classId: String,
        subjectName: String,
        subjectCode: String,
        department: String,
        semester: Int,
        students: Int
    ): Response<Unit> {

        val data = mapOf(
            "Teacher Uid" to teacherUid,
            "Class Id" to classId,
            "Subject Name" to subjectName,
            "Subject Code" to subjectCode,
            "Department" to department,
            "Semester" to semester,
            "Student strength" to students
        )

        return try {
            withTimeout(10000) {
                firebaseDB.collection("Classroom").document(teacherUid)
                    .collection(semester.toString()).document(classId).set(data).await()
                Response.Success()
            }
        } catch (e: TimeoutCancellationException) {
            Response.Failure("Operation timed out")
        } catch (e: Exception) {
            Response.Failure(getErrorMassage(e))
        }

    }

    private fun getErrorMassage(e: Exception): String {
        val colonIndex = e.toString().indexOf(":")
        return e.toString().substring(colonIndex + 2)
    }

}