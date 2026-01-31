package com.example.remedialucp2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.remedialucp2.database.BookDatabase
import com.example.remedialucp2.database.DatabaseHelper
import com.example.remedialucp2.models.Book
import com.example.remedialucp2.models.BookStatus
import com.example.remedialucp2.models.Category
import com.example.remedialucp2.services.BookService
import com.example.remedialucp2.services.CategoryService
import com.example.remedialucp2.services.MigrationService
import com.example.remedialucp2.ui.theme.RemedialUCP2Theme
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = BookDatabase.getDatabase(this)
        val bookService = BookService(database)
        val categoryService = CategoryService(database)
        val databaseHelper = DatabaseHelper(database)
        val migrationService = MigrationService()

        enableEdgeToEdge()
        setContent {
            RemedialUCP2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        MainContent(
                            bookService = bookService,
                            categoryService = categoryService,
                            databaseHelper = databaseHelper,
                            migrationService = migrationService,
                            modifier = Modifier.padding(innerPadding),
                            database = database
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent(
    bookService: BookService,
    categoryService: CategoryService,
    databaseHelper: DatabaseHelper,
    migrationService: MigrationService,
    database: BookDatabase,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf("welcome") }

    when (currentScreen) {
        "welcome" -> WelcomeScreen(
            modifier = modifier,
            onEnterClick = { currentScreen = "dashboard" }
        )
        "dashboard" -> DashboardScreen(
            bookService = bookService,
            categoryService = categoryService,
            databaseHelper = databaseHelper,
            migrationService = migrationService,
            database = database,
            onBackClick = { currentScreen = "welcome" },
            modifier = modifier
        )
    }
}

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier, onEnterClick: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.List,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Remedial UCP 2",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Digital Library Management System",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        
        Button(
            onClick = onEnterClick,
            modifier = Modifier.padding(top = 48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Mulai Kelola Data", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    bookService: BookService,
    categoryService: CategoryService,
    databaseHelper: DatabaseHelper,
    migrationService: MigrationService,
    database: BookDatabase,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showAddBookDialog by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Sistem Siap") }
    var isMigrating by remember { mutableStateOf(false) }
    var migrationProgress by remember { mutableIntStateOf(0) }

    // Observers
    val categories by database.categoryDao().getAllCategories().collectAsState(initial = emptyList())
    val books by database.bookDao().getAllBooks().collectAsState(initial = emptyList())

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Library Dashboard", fontWeight = FontWeight.Bold) },
            actions = {
                TextButton(onClick = onBackClick) { Text("Keluar") }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Action Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showAddCategoryDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Kategori")
                    }
                    Button(
                        onClick = { showAddBookDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Buku Baru")
                    }
                }
            }

            // Migration Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Migrasi Data Asinkron", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isMigrating) {
                            CircularProgressIndicator(
                                progress = { migrationProgress / 100f },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Text("Memproses: $migrationProgress%", modifier = Modifier.align(Alignment.CenterHorizontally))
                        } else {
                            Button(onClick = {
                                isMigrating = true
                                migrationService.migrateOldData(
                                    oldData = List(50) { it },
                                    onProgress = { migrationProgress = it },
                                    onComplete = { 
                                        isMigrating = false
                                        statusMessage = "Migrasi Berhasil"
                                    }
                                )
                            }, modifier = Modifier.fillMaxWidth()) {
                                Text("Jalankan Migrasi")
                            }
                        }
                    }
                }
            }

            // Status Bar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text("Status: $statusMessage", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Lists
            item { Text("Kategori Tersedia (${categories.size})", fontWeight = FontWeight.Bold) }
            items(categories) { category ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(category.name, modifier = Modifier.padding(12.dp))
                }
            }

            item { Text("Daftar Buku (${books.size})", fontWeight = FontWeight.Bold) }
            items(books) { book ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(book.title, fontWeight = FontWeight.Bold)
                        Text("ISBN: ${book.isbn}", style = MaterialTheme.typography.bodySmall)
                        Text("Status: ${book.status}", color = if(book.status == BookStatus.AVAILABLE) Color.Blue else Color.Red)
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { name ->
                scope.launch {
                    val res = categoryService.addCategory(Category(UUID.randomUUID().toString(), name))
                    statusMessage = if (res.isSuccess) "Kategori '$name' ditambahkan" else res.exceptionOrNull()?.message ?: "Gagal"
                    showAddCategoryDialog = false
                }
            }
        )
    }

    if (showAddBookDialog) {
        AddBookDialog(
            categories = categories,
            onDismiss = { showAddBookDialog = false },
            onConfirm = { title, isbn, catId ->
                scope.launch {
                    val res = bookService.addBook(Book(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        isbn = isbn,
                        categoryId = catId,
                        physicalCopyId = "PC-${UUID.randomUUID().toString().take(5)}",
                        status = BookStatus.AVAILABLE
                    ))
                    statusMessage = if (res.isSuccess) "Buku '$title' ditambahkan" else res.exceptionOrNull()?.message ?: "Gagal"
                    showAddBookDialog = false
                }
            }
        )
    }
}

@Composable
fun AddCategoryDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tambah Kategori", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Kategori") })
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Button(onClick = { if(name.isNotBlank()) onConfirm(name) }) { Text("Simpan") }
                }
            }
        }
    }
}

@Composable
fun AddBookDialog(categories: List<Category>, onDismiss: () -> Unit, onConfirm: (String, String, String?) -> Unit) {
    var title by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tambah Buku Baru", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul Buku") })
                OutlinedTextField(value = isbn, onValueChange = { isbn = it }, label = { Text("ISBN") })
                Text("Pilih Kategori (Opsional):", style = MaterialTheme.typography.bodySmall)
                LazyColumn(modifier = Modifier.height(100.dp)) {
                    items(categories) { cat ->
                        TextButton(onClick = { onConfirm(title, isbn, cat.id) }) { Text(cat.name) }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Button(onClick = { if(title.isNotBlank()) onConfirm(title, isbn, null) }) { Text("Simpan Tanpa Kategori") }
                }
            }
        }
    }
}
