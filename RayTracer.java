public void raytrace(float startX, float startY, float endX, float endY)
{
	//The exact x coordinate of the square that the first point is in
	int x = (int) Math.floor(startX);
	//The exact y coordinate of the square that the first point is in
	int y = (int) Math.floor(startY);
	
	//In which direction is the ray pointing? -1 if the second point is to the left of the first point, 1 otherwise
	int stepX = (endX - startX) < 0 ? -1 : 1;
	//In which direction is the ray pointing? -1 if the second point is below the first point, 1 otherwise
	int stepY = (endY - startY) < 0 ? -1 : 1;
	
	//How much percent of the total x distance does the ray need to travel to enter the next square on the x axis 
	float tMaxX = (stepX < 0 ? startX - x : 1 + x - startX) / Math.abs(endX - startX);
	//How much percent of the total y distance does the ray need to travel to enter the next square on the y axis
	float tMaxY = (stepY < 0 ? startY - y : 1 + y - startY) / Math.abs(endY - startY);
	
	//The percent of the total x distance that one square takes up
	float tDeltaX = 1 / Math.abs(endX - startX);
	//The percent of the total y distance that one square takes up
	float tDeltaY = 1 / Math.abs(endY - startY);
	
	//While the ray-tracer has travelled less than 100% of the total x axis distance and the total y axis distance
	while (tMaxX < 1 || tMaxY < 1)
	{
		//If the ray-tracer has travelled percentually less on the x axis than on the y axis 
		if (tMaxX < tMaxY)
		{
			//Add the percent of the total x distance one square takes up to the total percent travelled on the x axis
			tMaxX += tDeltaX;
			//Move the x coordinate to the next square on the x axis
			x += stepX;
		} else //Otherwise
		{
			//Add the percent of the total y distance one square takes up to the total percent travelled on the y axis
			tMaxY += tDeltaY;
			//Move the y coordinate to the next square on the y axis
			y += stepY;
		}
		//Select this square
		select(x, y, cell_size);
	}
}