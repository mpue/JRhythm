// Used for shadow lookup
varying vec4 ShadowCoord;



void main()
{


     	ShadowCoord = gl_TextureMatrix[6] * gl_Vertex;
  
		gl_Position = ftransform();

		gl_FrontColor = gl_Color;
}

