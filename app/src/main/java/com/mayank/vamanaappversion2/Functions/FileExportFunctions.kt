import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.mayank.vamanaappversion2.Modals.Patient
import com.mayank.vamanaappversion2.Modals.Question
import com.mayank.vamanaappversion2.Modals.PatientQuestion
import com.mayank.vamanaappversion2.Modals.QuestionDetail
import com.mayank.vamanaappversion2.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun exportPatientsToCSV(
    context: Context,
    patients: List<Patient>,
    questions: List<Question>,
    analysisQuestions: List<QuestionDetail>
) {
    val fileName = "PatientsData.csv"
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

    // Collect all unique questions and analysis questions as headers
    val allQuestions = questions.flatMap { it.questions }.distinctBy { it.id }
    val allAnalysisQuestions = analysisQuestions.distinctBy { it.id }

    // Construct CSV Headers
    val headers = listOf("UHID", "Name", "Age", "Date of Admission") +
            allQuestions.map { "Question: "+it.question } +
            allAnalysisQuestions.map { "Analysis: "+it.question }

    // Writing to CSV file
    file.bufferedWriter().use { writer ->
        // Write headers
        writer.write(headers.joinToString(","))
        writer.newLine()

        // Write data rows
        patients.forEach { patient ->
            val rowData = mutableListOf(
                patient.uhid,
                patient.name,
                patient.age.toString(),
                patient.dateOfAdmission
            )

            // Match answers with respective questions, join answers with '-'
            allQuestions.forEach { question ->
                val matchingAnswer = patient.questions?.find { it.questionUID == question.id }?.answers?.joinToString("-")
                rowData.add(matchingAnswer ?: "N/A")
            }

            // Match analysis answers with respective analysis questions, join answers with '-'
            allAnalysisQuestions.forEach { analysisQuestion ->
                val matchingAnalysisAnswer = patient.Analysis?.find { it.questionUID == analysisQuestion.id }?.answers?.joinToString("-")
                rowData.add(matchingAnalysisAnswer ?: "N/A")
            }

            writer.write(rowData.joinToString(","))
            writer.newLine()
        }
    }

    Toast.makeText(context, "CSV Exported: ${file.absolutePath}", Toast.LENGTH_SHORT).show()

    // Share the file
    shareCSVFile(context, file)
}

fun shareCSVFile(context: Context, file: File) {
    val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share CSV via"))
}


fun copyPdfToInternalStorage(context: Context, fileName: String , pdfno: Int): File? {
    val file = File(context.filesDir, fileName)
    var fileresource = if (pdfno == 1)
    {
        R.raw.avara_samsarjana_karma
    }
    else if(pdfno == 2) {
        R.raw.madhyam_samsarjana_karma
    }
    else
    {
        R.raw.pravar_shuddhi_samsarjana_krama
    }
    if (!file.exists()) { // Copy only if not already copied
        try {
            context.resources.openRawResource(fileresource).use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
    return file
}


fun sharePdf(context: Context,pdfno:Int) {
    var filename = if (pdfno == 1)
    {
        "avara_samsarjana_karma.pdf"
    }
    else if(pdfno == 2)
    {
        "madhyam_samsarjana_karma.pdf"
    } else {
        "pravar_shuddhi_samsarjana_krama.pdf"
    }
    val pdfFile = copyPdfToInternalStorage(context, filename , pdfno) ?: return
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", pdfFile)

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share PDF"))
}

