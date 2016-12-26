package com.activeandroid.sebbia.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.activeandroid.sebbia.Model;
import com.activeandroid.sebbia.annotation.Column;
import com.activeandroid.sebbia.annotation.DoNotGenerate;

public final class AnnotationProcessor extends AbstractProcessor {

	private static final String MODEL = "model";
	private static final String CURSOR = "cursor";
	private static final String CONTENT_VALUES = "contentValues";
	private static final String COLUMNS_ORDERED = "columnsOrdered";
	private static final String STATEMENT = "statement";
	private static final String COLUMNS = "columns";

	private RoundEnvironment env;

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
		this.env = env;
		if (annotations.size() > 0) {
			parseColumns();
		}
		return true;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> supportedTypes = new HashSet<String>();
		supportedTypes.add(Column.class.getCanonicalName());
		return supportedTypes;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	private void parseColumns() {
		Set<? extends Element> columns = env.getElementsAnnotatedWith(Column.class);
		Map<TypeElement, Set<VariableElement>> tables = new HashMap<TypeElement, Set<VariableElement>>();
		for (Element element : columns) {
			if (element instanceof VariableElement == false || element.getKind() != ElementKind.FIELD) {
				error("@Column annotation should be applied only to local variables", element);
				continue;
			}
			VariableElement columnElement = (VariableElement) element;

			TypeElement tableElement = null;
			if (element.getEnclosingElement() instanceof TypeElement) {
				tableElement = (TypeElement) element.getEnclosingElement();
			} else {
				error("@Column annotation located not inside of class", element);
				continue;
			}
				
			if (checkTableModifiers(tableElement) == false)
				continue;
			
			if (checkColumnModifiers(columnElement) == false)
				continue;

			Set<VariableElement> columnsElements = tables.get(tableElement);
			if (columnsElements == null) {
				columnsElements = new HashSet<VariableElement>();
				tables.put(tableElement, columnsElements);
			}

			columnsElements.add(columnElement);

		}

		for (TypeElement table : tables.keySet()) {
			generate(table, tables.get(table));
		}
	}

	private void generate(TypeElement tableElement, Set<VariableElement> columns) {
		String packageName = processingEnv.getElementUtils().getPackageOf(tableElement).getQualifiedName().toString();
		String className = tableElement.getQualifiedName().toString();
		String fillerClassName = getClassName(tableElement, packageName) + ModelFiller.SUFFIX;

		try {
			JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName + "." + fillerClassName, tableElement);
			Writer writer = jfo.openWriter();
			writer.write("//Generated by ActiveAndroid. Do not modify\n");
			writer.write("package " + packageName + ";\n\n");
			
			writer.write("import android.database.sqlite.SQLiteStatement;\n");
			writer.write("import java.util.ArrayList;\n");
			writer.write("import java.util.Arrays;\n");
			writer.write("import java.util.List;\n\n");
			writer.write("import java.util.Map;\n\n");

			writer.write("import com.activeandroid.sebbia.internal.ModelHelper;\n");
			writer.write("import com.activeandroid.sebbia.internal.ModelFiller;\n");
			writer.write("\n");
			writer.write("public class " + fillerClassName + " extends ModelFiller {\n\n");
			writer.write("  public void loadFromCursor(com.activeandroid.sebbia.Model genericModel, android.database.Cursor " + CURSOR + ") {\n");
			writer.write("    if (superModelFiller != null)\n");
			writer.write("       superModelFiller.loadFromCursor(genericModel, " + CURSOR + ");\n");
			writer.write("    List<String> " + COLUMNS_ORDERED + " = new ArrayList<String>(Arrays.asList(" + CURSOR + ".getColumnNames()));\n");
			writer.write("    " + className + " " + MODEL + " = (" + className + ") genericModel;\n");
			writer.write(getLoadFromCursorCode(columns));
			writer.write("  }\n\n");

			
			writer.write("  public void fillContentValues(com.activeandroid.sebbia.Model genericModel, android.content.ContentValues " + CONTENT_VALUES + ") {\n");
			writer.write("    if (superModelFiller != null)\n");
			writer.write("       superModelFiller.fillContentValues(genericModel, " + CONTENT_VALUES + ");\n");
			writer.write("    " + className + " " + MODEL + " = (" + className + ") genericModel;\n");
			writer.write(getFillContentValuesCode(columns));
			writer.write("  }\n");
			
			writer.write("  public void bindStatement(com.activeandroid.sebbia.Model genericModel, SQLiteStatement " + STATEMENT + ", Map<String, Integer> " + COLUMNS + ") {\n");
			writer.write("    if (superModelFiller != null)\n");
			writer.write("       superModelFiller.bindStatement(genericModel, " + STATEMENT + ", " + COLUMNS + ");\n");
			writer.write("    " + className + " " + MODEL + " = (" + className + ") genericModel;\n");
			writer.write(getBindStatementCode(columns));
			writer.write("  }\n");

			writer.write("}");
			writer.flush();
			writer.close();
		} catch (IOException exception) {
			processingEnv.getMessager().printMessage(Kind.ERROR, exception.getMessage());
		}
	}

	private String getLoadFromCursorCode(Set<VariableElement> columns) {
		StringBuilder stringBuilder = new StringBuilder();

		for (VariableElement column : columns) {
			Column annotation = column.getAnnotation(Column.class);

			String fieldName = annotation.name();

			if (fieldName == null || fieldName.isEmpty())
				fieldName = column.getSimpleName().toString();

			TypeMirror typeMirror = column.asType();
			String type = getClassString(typeMirror, typeMirror instanceof DeclaredType);
			String getColumnIndex = COLUMNS_ORDERED + ".indexOf(\"" + fieldName + "\")";

			String setValue = "    " + MODEL + "." + column.getSimpleName() + " = " + CURSOR;

			if (isTypeOf(typeMirror, Integer.class) || isTypeOf(typeMirror, int.class))
				stringBuilder.append(setValue + ".getInt(" + getColumnIndex + ");\n");
			else if (isTypeOf(typeMirror, Byte.class) || isTypeOf(typeMirror, byte.class))
				stringBuilder.append(setValue + ".getInt(" + getColumnIndex + ");\n");
			else if (isTypeOf(typeMirror, Short.class) || isTypeOf(typeMirror, short.class))
				stringBuilder.append("    " + MODEL + "." + column.getSimpleName() + " = (short) " + CURSOR + ".getInt(" + getColumnIndex + ");\n");
			else if (isTypeOf(typeMirror, Long.class) || isTypeOf(typeMirror, long.class))
				stringBuilder.append(setValue + ".getLong(" + getColumnIndex + ");\n");
			else if (isTypeOf(typeMirror, Float.class) || isTypeOf(typeMirror, float.class))
				stringBuilder.append(setValue + ".getFloat(" + getColumnIndex + ");\n");
			else if (isTypeOf(typeMirror, Double.class) || isTypeOf(typeMirror, double.class))
				stringBuilder.append(setValue + ".getDouble(" + getColumnIndex + ");\n");
			else if (isTypeOf(typeMirror, Boolean.class) || isTypeOf(typeMirror, boolean.class))
				stringBuilder.append(setValue + ".getInt(" + getColumnIndex + ") != 0;\n");
			else if (isTypeOf(typeMirror, Character.class) || isTypeOf(typeMirror, char.class))
				stringBuilder.append(setValue + ".getString(" + getColumnIndex + ");\n");
			else if (isTypeOf(typeMirror, String.class))
				stringBuilder.append(setValue + ".getString(" + getColumnIndex + ");\n");
			else if (isTypeOf(typeMirror, Byte[].class) || isTypeOf(typeMirror, byte[].class))
				stringBuilder.append(setValue + ".getBlob(" + getColumnIndex + ");\n");
			else {
				processingEnv.getMessager().printMessage(Kind.NOTE, "Guessing what type is at " + typeMirror.toString(), null);
				stringBuilder.append("    if (ModelHelper.isSerializable(" + type + ")) {\n");
				stringBuilder.append("      " + MODEL + "." + column.getSimpleName() + " = (" + typeMirror.toString() + ") ModelHelper.getSerializable(cursor, " + type + ", " + getColumnIndex + ");\n");
				stringBuilder.append("    } else {\n");
				stringBuilder.append("      " + MODEL + "." + column.getSimpleName() + " = ");
				if (isTypeOf(typeMirror, Model.class))
					stringBuilder.append("(" + typeMirror.toString() + ") ModelHelper.getModel(cursor, " + type + ", " + getColumnIndex + ");\n");
				else if (isTypeOf(typeMirror, Enum.class))
					stringBuilder.append("(" + typeMirror.toString() + ") ModelHelper.getEnum(cursor, " + type + ", " + getColumnIndex + ");\n");
				else
					stringBuilder.append(" null;\n");
				
				stringBuilder.append("    }\n");
			}
		}
		return stringBuilder.toString();
	}

	private String getFillContentValuesCode(Set<VariableElement> columns) {
		StringBuilder stringBuilder = new StringBuilder();

		for (VariableElement column : columns) {
			Column annotation = column.getAnnotation(Column.class);

			String fieldName = annotation.name();

			if (fieldName == null || fieldName.isEmpty())
				fieldName = column.getSimpleName().toString();
			
			TypeMirror typeMirror = column.asType();
			boolean notPrimitiveType = typeMirror instanceof DeclaredType;
			String type = getClassString(typeMirror, notPrimitiveType);
			String getValue = MODEL + "." + column.getSimpleName();
			
			boolean hasDefault = annotation.defaultValue() != null && annotation.defaultValue().isEmpty() == false;
			String emptySpace = "    ";
			if (notPrimitiveType) {
				stringBuilder.append(emptySpace + "if (" + getValue + " != null) {\n");
				emptySpace += "  ";
 			}
			String putValue = emptySpace + CONTENT_VALUES + ".put(\"" + fieldName + "\", " + getValue;
			
			if (isTypeOf(typeMirror, Integer.class) || isTypeOf(typeMirror, int.class))
				stringBuilder.append(putValue + ");\n");
			else if (isTypeOf(typeMirror, Byte.class) || isTypeOf(typeMirror, byte.class))
				stringBuilder.append(putValue + ");\n");
			else if (isTypeOf(typeMirror, Short.class) || isTypeOf(typeMirror, short.class))
				stringBuilder.append(putValue + ");\n");
			else if (isTypeOf(typeMirror, Long.class) || isTypeOf(typeMirror, long.class))
				stringBuilder.append(putValue + ");\n");
			else if (isTypeOf(typeMirror, Float.class) || isTypeOf(typeMirror, float.class))
				stringBuilder.append(putValue + ");\n");
			else if (isTypeOf(typeMirror, Double.class) || isTypeOf(typeMirror, double.class))
				stringBuilder.append(putValue + ");\n");
			else if (isTypeOf(typeMirror, Boolean.class) || isTypeOf(typeMirror, boolean.class))
				stringBuilder.append(putValue + ");\n");
			else if (isTypeOf(typeMirror, Character.class) || isTypeOf(typeMirror, char.class))
				stringBuilder.append(putValue + ".toString());\n");
			else if (isTypeOf(typeMirror, String.class))
				stringBuilder.append(putValue + ".toString());\n");
			else if (isTypeOf(typeMirror, Byte[].class) || isTypeOf(typeMirror, byte[].class))
				stringBuilder.append(putValue + ");\n");
			else {
				stringBuilder.append(emptySpace + "if (ModelHelper.isSerializable(" + type + ")) {\n");
				stringBuilder.append(emptySpace + "  ModelHelper.setSerializable(" + CONTENT_VALUES + ", " + type + ", " + getValue + ", \"" + fieldName + "\");\n");  
				stringBuilder.append(emptySpace + "} else {\n");
				stringBuilder.append(emptySpace + "  " + CONTENT_VALUES + ".");
				if (isTypeOf(typeMirror, Model.class))
					stringBuilder.append("put(\"" + fieldName + "\", " + getValue + ".getNewsId());\n");
				else if (isTypeOf(typeMirror, Enum.class))
					stringBuilder.append("put(\"" + fieldName + "\", " + getValue + ".name());\n");
				else
					stringBuilder.append("putNull(\"" + fieldName + "\");\n");
				stringBuilder.append(emptySpace + "}\n");
			}
			if (notPrimitiveType) {
				if (hasDefault == false) {
					stringBuilder.append("    } else {\n");
					stringBuilder.append("      " + CONTENT_VALUES + ".putNull(\"" + fieldName +  "\");\n");
				}
				stringBuilder.append("    }\n");
			}
		}
		return stringBuilder.toString();
	}
	
	private String getBindStatementCode(Set<VariableElement> columns) {
		StringBuilder stringBuilder = new StringBuilder();

		for (VariableElement column : columns) {
			Column annotation = column.getAnnotation(Column.class);

			String fieldName = annotation.name();

			if (fieldName == null || fieldName.isEmpty())
				fieldName = column.getSimpleName().toString();
			
			TypeMirror typeMirror = column.asType();
			boolean notPrimitiveType = typeMirror instanceof DeclaredType;
			String type = getClassString(typeMirror, notPrimitiveType);
			
			String getValue = MODEL + "." + column.getSimpleName();
			
			String columnIndex = COLUMNS + ".get(\"" + fieldName + "\")"; 
			String emptySpace = "    ";
			
			if (notPrimitiveType) {
				stringBuilder.append(emptySpace + "if (" + getValue + " != null) {\n");
				emptySpace += "  ";
 			}
			String bind = emptySpace + STATEMENT + ".bind";
			
			if (isTypeOf(typeMirror, Integer.class) || isTypeOf(typeMirror, int.class))
				stringBuilder.append(bind + "Long(" + columnIndex + ", " + getValue + ");\n");
			else if (isTypeOf(typeMirror, Byte.class) || isTypeOf(typeMirror, byte.class))
				stringBuilder.append(bind + "Long(" + columnIndex + ", " + getValue + ");\n");
			else if (isTypeOf(typeMirror, Short.class) || isTypeOf(typeMirror, short.class))
				stringBuilder.append(bind + "Long(" + columnIndex + ", " + getValue + ");\n");
			else if (isTypeOf(typeMirror, Long.class) || isTypeOf(typeMirror, long.class))
				stringBuilder.append(bind + "Long(" + columnIndex + ", " + getValue + ");\n");
			else if (isTypeOf(typeMirror, Float.class) || isTypeOf(typeMirror, float.class))
				stringBuilder.append(bind + "Double(" + columnIndex + ", " + getValue + ");\n");
			else if (isTypeOf(typeMirror, Double.class) || isTypeOf(typeMirror, double.class))
				stringBuilder.append(bind + "Double(" + columnIndex + ", " + getValue + ");\n");
			else if (isTypeOf(typeMirror, Boolean.class) || isTypeOf(typeMirror, boolean.class))
				stringBuilder.append(bind + "Long(" + columnIndex + ", " + getValue + " ? 1 : 0);\n");
			else if (isTypeOf(typeMirror, Character.class) || isTypeOf(typeMirror, char.class))
				stringBuilder.append(bind + "String(" + columnIndex + ", " + getValue + ".toString());\n");
			else if (isTypeOf(typeMirror, String.class))
				stringBuilder.append(bind + "String(" + columnIndex + ", " + getValue + ".toString());\n");
			else if (isTypeOf(typeMirror, Byte[].class) || isTypeOf(typeMirror, byte[].class))
				stringBuilder.append(bind + "Blob(" + columnIndex + ", " + getValue + ");\n");
			else {
				boolean isModel = isTypeOf(typeMirror, Model.class);
				boolean isEnum = isTypeOf(typeMirror, Enum.class);
				if (isModel || isEnum) {
					stringBuilder.append(emptySpace + "if (ModelHelper.isSerializable(" + type + ")) {\n");
					stringBuilder.append(emptySpace + "  ModelHelper.setSerializable(" + STATEMENT + ", " + COLUMNS + ", " + type + ", " + getValue + ", \"" + fieldName + "\");\n");  
					stringBuilder.append(emptySpace + "} else {\n");
					stringBuilder.append(emptySpace + "  " + STATEMENT + ".bind");
					if (isModel)
						stringBuilder.append("Long(" + columnIndex + ", " + getValue + ".getNewsId());\n");
					else if (isEnum)
						stringBuilder.append("String(" + columnIndex + ", " + getValue + ".name());\n");
					stringBuilder.append(emptySpace + "}\n");
				} else {
					stringBuilder.append(emptySpace + "if (ModelHelper.isSerializable(" + type + "))\n");
					stringBuilder.append(emptySpace + "  ModelHelper.setSerializable(" + STATEMENT + ", " + COLUMNS + ", " + type + ", " + getValue + ", \"" + fieldName + "\");\n");
				}
			}
			if (notPrimitiveType)
				stringBuilder.append("    }\n");
		}
		return stringBuilder.toString();
	}

	private String getClassString(TypeMirror typeMirror, boolean notPrimitiveType) {
		String type = typeMirror.toString() + ".class";
		if (notPrimitiveType) {
			DeclaredType declaredType = (DeclaredType) typeMirror;
			List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
			if (typeArguments != null && typeArguments.size() > 0)
				type = ((TypeElement) declaredType.asElement()).getQualifiedName() + ".class";
		}
		return type;
	}

	private boolean isTypeOf(TypeMirror typeMirror, Class<?> type) {
		if (type.getCanonicalName().equals(typeMirror.toString()))
			return true;

		if (typeMirror instanceof DeclaredType == false)
			return false;

		DeclaredType declaredType = (DeclaredType) typeMirror;
		Element element = declaredType.asElement();
		if (element instanceof TypeElement == false)
			return false;

		TypeElement typeElement = (TypeElement) element;
		if (type == Enum.class)
			return typeElement.getKind() == ElementKind.ENUM;
		
		TypeMirror superType = typeElement.getSuperclass();
		if (isTypeOf(superType, type))
			return true;
		return false;
	}

	private boolean checkTableModifiers(TypeElement table) {
		if (table.getModifiers().contains(Modifier.PRIVATE)) {
			error("Classes marked with @Table cannot be private", table);
			return false;
		}

		if (table.getKind() != ElementKind.CLASS) {
			error("Only classes can be marked with @Table annotation", table);
			return false;
		}
		
		if (table.getAnnotation(DoNotGenerate.class) != null)
			return false;

		return true;
	}

	private boolean checkColumnModifiers(VariableElement column) {

		if (column.getModifiers().contains(Modifier.PRIVATE)) {
			error("Field marked with @Column cannot be private", column);
			return false;
		}

		if (column.getModifiers().contains(Modifier.FINAL)) {
			error("Field marked with @Column cannot be final", column);
			return false;
		}

		if (column.getModifiers().contains(Modifier.STATIC)) {
			error("Field marked with @Column cannot be static", column);
			return false;
		}

		return true;
	}

	private void error(String message, Element element) {
		processingEnv.getMessager().printMessage(Kind.ERROR, message, element);
	}

	private static String getClassName(TypeElement type, String packageName) {
		int packageLen = packageName.length() + 1;
		return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
	}
}
