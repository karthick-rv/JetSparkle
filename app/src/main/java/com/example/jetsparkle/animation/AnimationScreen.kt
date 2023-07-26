package com.example.jetsparkle.animation

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.jetsparkle.R

@Composable
fun AnimationScreen() {

    Column {
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f)){
            AnimatableColor()
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)){
            AnimateDpAsState()
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f)){
            AnimateAsFloatContent()
        }

        Spacer(modifier = Modifier.size(50.dp))

        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)){
            TransitionAnimation()
        }
    }

}


@Composable
fun AnimatableColor() {
    var isAnimated by remember { mutableStateOf(false) }

    val color = remember { Animatable(Color.DarkGray) }


    LaunchedEffect(isAnimated){
        color.animateTo(if(isAnimated) Color.Green else Color.Blue, animationSpec = tween(2000))
    }

    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .background(color.value)
    )
    Button(
        onClick = { isAnimated = !isAnimated },
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text(text = "Animate Color")
    }

}


@Composable
fun CircleImage(imageSize: Dp) {
    Image(
        painter = painterResource(R.drawable.ic_launcher_background),
        contentDescription = "Circle Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(imageSize)
            .clip(CircleShape)
            .border(5.dp, Color.Gray, CircleShape)
    )
}

@Composable
private fun AnimateDpAsState() {
    val isNeedExpansion = rememberSaveable{ mutableStateOf(false) }

    val animatedSizeDp: Dp by animateDpAsState(targetValue = if (isNeedExpansion.value) 150.dp else 100.dp)

    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CircleImage(animatedSizeDp)
        Spacer(modifier = Modifier.size(50.dp))
        Button(
            onClick = {
                isNeedExpansion.value = !isNeedExpansion.value
            },
            modifier = Modifier
                .padding(top = 50.dp)
                .width(100.dp)
        ) {
            Text(text = "Animate circle")
        }
    }
}

@Composable
private fun AnimateAsFloatContent() {
    var isRotated by rememberSaveable { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isRotated) 360F else 0f,
        animationSpec = tween(durationMillis = 2500)
    )
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(R.drawable.fan),
            contentDescription = "fan",
            modifier = Modifier
                .rotate(rotationAngle)
                .size(100.dp)
        )
        Spacer(modifier = Modifier.size(50.dp))
        Button(
            onClick = { isRotated = !isRotated },
            modifier = Modifier
                .padding(top = 50.dp)
                .width(100.dp)
        ) {
            Text(text = "Rotate Fan")
        }
    }
}


@Composable
private fun TransitionAnimation() {
    var isAnimated by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = isAnimated, label = "transition")

    val rocketOffset by transition.animateOffset(transitionSpec = {
        if (this.targetState) {
            tween(1000) // launch duration

        } else {
            tween(1500) // land duration
        }
    }, label = "rocket offset") { animated ->
        if (animated) Offset(300f, 0f) else Offset(0f, 0f)
    }

    val rocketSize by transition.animateDp(transitionSpec = {
        tween(1000)
    }, "") { animated ->
        if (animated) 75.dp else 150.dp
    }


    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.balloon),
            contentDescription = "Rocket",
            modifier = Modifier
                .size(rocketSize)
                .alpha(1.0f)
                .offset(rocketOffset.x.dp, rocketOffset.y.dp)
        )
        Button(
            onClick = { isAnimated = !isAnimated },
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(text = if (isAnimated) "Land " else "Launch ")
        }
    }
}
