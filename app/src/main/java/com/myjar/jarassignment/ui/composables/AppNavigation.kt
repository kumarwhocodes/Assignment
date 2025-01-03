package com.myjar.jarassignment.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.ui.vm.JarViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    viewModel: JarViewModel,
) {
    val navController = rememberNavController()
    val navigate = remember { mutableStateOf<String>("") }

    NavHost(modifier = modifier, navController = navController, startDestination = "item_list") {
        composable("item_list") {
            ItemListScreen(
                viewModel = viewModel,
                onNavigateToDetail = { selectedItem -> navigate.value = selectedItem },
                navigate = navigate,
                navController = navController
            )
        }
        composable("item_detail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            ItemDetailScreen(itemId = itemId, viewModel = viewModel)
        }
    }
}

@Composable
fun ItemListScreen(
    viewModel: JarViewModel,
    onNavigateToDetail: (String) -> Unit,
    navigate: MutableState<String>,
    navController: NavHostController
) {
    val items = viewModel.listStringData.collectAsState()
    val searchQuery = viewModel.searchQuery.collectAsState()
    val filteredItems = viewModel.filteredList.collectAsState()

    if (navigate.value.isNotBlank()) {
        val currRoute = navController.currentDestination?.route.orEmpty()
        if (!currRoute.contains("item_detail")) {
            navController.navigate("item_detail/${navigate.value}")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            value = searchQuery.value,
            onValueChange = {
                viewModel.updateSearchQuery(it)
            },
            label = {
                Text(text = "Search items")
            })
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(filteredItems.value) { item ->
                ItemCard(item = item, onClick = { onNavigateToDetail(item.id) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

    }

}

@Composable
fun ItemCard(item: ComputerItem, onClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable { onClick() }) {
        Text(text = item.name, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun ItemDetailScreen(itemId: String?, viewModel: JarViewModel) {
    LaunchedEffect(itemId) {
        itemId?.let { viewModel.fetchItemDetails(it) }
    }

    val itemDetails by viewModel.itemDetails.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        if (itemDetails != null) {
            Text(text = "Item Details for ID: ${itemDetails!!.id}")
            Text(text = "Name: ${itemDetails!!.name}")
            itemDetails!!.data?.let { itemData ->
                Text(text = "Screen size: ${itemData.screenSize}")
                Text(text = "Capacity: ${itemData.capacity}")
                Text(text = "Color: ${itemData.color}")
            }
        } else {
            Text(text = "Loading...")
        }
    }
}

